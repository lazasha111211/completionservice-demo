package com.example.completionservicedemo.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 保单信息
 * @author: lazasha
 * @date: 2020/1/17  10:22
 **/
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PolicyModel implements Serializable {
    private String policyNo;
    private String policyHolder;
    private List<Insured> insuredList;
    private List<Risk> riskList;
}
