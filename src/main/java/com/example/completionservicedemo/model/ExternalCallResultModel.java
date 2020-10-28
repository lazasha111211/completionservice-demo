package com.example.completionservicedemo.model;

import lombok.*;

import java.io.Serializable;

/**
 * @description: TODO
 * @author: lazasha
 * @date: 2020/1/17  10:54
 **/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExternalCallResultModel implements Serializable {
    private String idcard;
    private float score;
}
