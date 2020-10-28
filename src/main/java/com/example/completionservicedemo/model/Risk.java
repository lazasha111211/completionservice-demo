package com.example.completionservicedemo.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description: 险种信息
 * @author: lazasha
 * @date: 2020/1/17  10:24
 **/
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Risk implements Serializable {
    private String riskcode;
    private String riskname;
    private BigDecimal premium;
    private int mainFlag;
}
