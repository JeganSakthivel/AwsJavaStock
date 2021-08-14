package com.microservice.stock.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {

    @NotEmpty(message = "companyCode is mandatory")
    private String companyCode;

    @NotNull(message="stockPrice is mandatory")
    private BigDecimal stockPrice;

    @Null(message = "Date should not be provided")
    private Date date;

    @Null(message = "Time should not be provided")
    private Time time;

}
