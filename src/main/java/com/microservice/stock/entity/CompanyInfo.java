package com.microservice.stock.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class CompanyInfo  {


    private String companyCode;


    private String companyName;


    private String companyCeo;


    private Long companyTurnOver;


    private String companyWebsite;


    private String stockExchange;

}
