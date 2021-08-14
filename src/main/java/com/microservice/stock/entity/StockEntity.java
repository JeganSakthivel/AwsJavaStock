package com.microservice.stock.entity;



import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microservice.stock.util.DateTimeSerializer;
import com.microservice.stock.util.TimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;


@Entity
@Data
@Table(name="stock")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class StockEntity {

    @Id
    @GeneratedValue
    private int stockId;


    @Column(columnDefinition = "varchar(200)")
    private String companyCode;


    private BigDecimal stockPrice;

    @Null(message = "Date should not be provided")
    @Column(columnDefinition = "DATE default (CURRENT_DATE)")
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date date;

    @Null(message = "Time should not be provided")
    @Column(columnDefinition = "TIME default (CURRENT_TIME)")
    @JsonSerialize(using = TimeSerializer.class)
    private Time time;

}
