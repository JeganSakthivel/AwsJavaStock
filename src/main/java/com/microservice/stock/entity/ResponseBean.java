package com.microservice.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBean {

    private List<StockEntity> stockList;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private BigDecimal avgValue;
}
