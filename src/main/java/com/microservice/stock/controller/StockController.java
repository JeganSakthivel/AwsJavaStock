package com.microservice.stock.controller;

import com.microservice.stock.entity.*;
import com.microservice.stock.respository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController()
@RequestMapping("/")
@Transactional
public class StockController {

    Logger logger = LoggerFactory.getLogger(StockController.class);

    private final EntityManager em;
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    @Value("${COMPANY_SERVICE_URI:http://localhost:8000/api/v1.0/market/company/}")
    private String companyServiceHost;

    public StockController(StockRepository stockRepository, EntityManager em, RestTemplate restTemplate){
        this.stockRepository = stockRepository;
        this.em = em;
        this.restTemplate = restTemplate;
    }

    @GetMapping("test")
    public String test() {
        return "Hello Stock";
    }

    @PostMapping("add/{code}")
    public ResponseEntity<Object> saveStock(@PathVariable("code") String companyCode,
                                            @Valid @RequestBody StockDTO stock) {
        logger.debug("saveStock - companyCode {} " + companyCode);
        logger.debug("companyServiceHost uri - " + companyServiceHost+"info/"+companyCode);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(companyServiceHost +"info/{code}", String.class, createUriVariables(companyCode));
        if (responseEntity.getBody() != null) {
           return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
        }else {
            StockEntity persistStock = new StockEntity();
            persistStock.setStockPrice(stock.getStockPrice());
            persistStock.setCompanyCode(stock.getCompanyCode());
            StockEntity savedStock = stockRepository.saveAndFlush(persistStock);
            em.refresh(savedStock);
            logger.debug("savedStock - " + savedStock);
            return new ResponseEntity<>(savedStock, HttpStatus.CREATED);
        }
    }

    @GetMapping("{code}")
    public ResponseEntity<Object>  getLatestStockDetail(@PathVariable("code") String companyCode) throws Exception {
        logger.debug("getLatestStockDetail - companyCode {} "+ companyCode);
        Sort sort = Sort.by("date").descending()
                .and(Sort.by("time").descending());
        StockEntity stock = stockRepository.findFirstByCompanyCode(companyCode,sort);
        if (stock != null) {
            return new ResponseEntity<>(stock, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No Stock Found", HttpStatus.OK);
        }
    }

    @PostMapping("getCodeList")
    public ResponseBean getLatestStocksList(@RequestBody CodeListWrapper codeList) throws Exception {
        logger.debug("getLatestStocksList - companyCode {} "+ codeList);
        Map<String, StockEntity> stockMap = new HashMap<>();
        Sort sort = Sort.by("date").descending()
                .and(Sort.by("time").descending());
        List<StockEntity> stockList = stockRepository.findDistinctByCompanyCodeIn(codeList.getCodeList(), sort);
        logger.debug("stockList - {} "+ stockList);
        if (stockList!=null && stockList.size() >0) {
            stockMap =  stockList.stream().collect(
                    Collectors.toMap(x->x.getCompanyCode(),x->x,
                            (oldValue, newValue) -> oldValue
                    ));
            logger.debug("stockMap - {} "+ stockMap);
        } else {
            return null;
        }
        ResponseBean responseBean = new ResponseBean();
        List<StockEntity> newStockList = stockMap.values().stream().collect(Collectors.toList());
        responseBean.setStockList(newStockList);
        return responseBean;
    }

    @GetMapping("get/{code}/{startDate}/{endDate}")
    public ResponseBean getCompanyStocksRange(@PathVariable("code") String companyCode, @PathVariable("startDate") Date startDate, @PathVariable("endDate") Date endDate){
        logger.debug("getCompanyStocksRange - companyCode {} "+ companyCode);
        ResponseBean responseBean = new ResponseBean();
        responseBean.setStockList(stockRepository.findAllStockPriceforRange(companyCode,startDate,endDate));
        responseBean.setMinValue(stockRepository.minStockPriceforRange(companyCode,startDate,endDate));
        responseBean.setMaxValue(stockRepository.maxStockPriceForRange(companyCode,startDate,endDate));
        responseBean.setAvgValue(stockRepository.avgStockPriceForRange(companyCode,startDate,endDate));
        return responseBean;
    }

    @DeleteMapping("/delete/{code}")
    public void deleteStock(@PathVariable("code") String companyCode) {
        logger.debug("deleteStock - companyCode {} " + companyCode);
        stockRepository.deleteByCompanyCode(companyCode);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object>  handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            errors.add(error.getDefaultMessage());
        });
        logger.error("ValidationException - errors {} "+ errors);
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object>  handleAllExceptions(Exception ex) {
        List<String> errors = new ArrayList<>();
        errors.add( ex.getLocalizedMessage());
        logger.error("AllException - errors {} "+ errors);
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Object> handleConstraintViolation(TransactionSystemException e, WebRequest request) {
        List<String> errors = new ArrayList<>();


        if (e.getRootCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e.getRootCause();
            if (cve != null) {
                cve.getConstraintViolations().forEach(err -> {
                  errors.add(err.getMessageTemplate());
                });
            }
        } else {
            if (e.getRootCause() != null ) {
                errors.add(e.getRootCause().getLocalizedMessage());
            }
        }

        logger.error("handleConstraintViolation - errors {} " + errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> createUriVariables(String companyCode) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("code", companyCode);
        return uriVariables;
    }


}
