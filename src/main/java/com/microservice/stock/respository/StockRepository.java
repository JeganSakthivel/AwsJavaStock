package com.microservice.stock.respository;
import com.microservice.stock.entity.StockEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.sql.Date;

import java.util.List;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

    StockEntity findFirstByCompanyCode(String companyCode,Sort sort);

    List<StockEntity> findDistinctByCompanyCodeIn(List<String> companyCodes, Sort sort);

    Long deleteByCompanyCode(String companyCode);

    @Query("select s from StockEntity s where s.companyCode = :companyCode and s.date >= :startDate and s.date<= :endDate")
    List<StockEntity> findAllStockPriceforRange( @Param("companyCode") String companyCode,
                                                @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT min(s.stockPrice) FROM StockEntity s where s.companyCode = :companyCode and s.date >= :startDate and s.date<= :endDate")
    BigDecimal minStockPriceforRange(@Param("companyCode") String companyCode,
                                     @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT max(s.stockPrice) FROM StockEntity s where s.companyCode = :companyCode and s.date >= :startDate and s.date<= :endDate")
    BigDecimal maxStockPriceForRange(@Param("companyCode") String companyCode,
                                     @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT avg(s.stockPrice) FROM StockEntity s where s.companyCode = :companyCode and s.date >= :startDate and s.date<= :endDate")
    BigDecimal avgStockPriceForRange(@Param("companyCode") String companyCode,
                                     @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
