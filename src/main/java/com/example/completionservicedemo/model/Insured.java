package com.example.completionservicedemo.model;

import lombok.*;

import java.io.Serializable;

/**
 * @description: TODO
 * @author: lazasha
 * @date: 2020/1/17  10:23
 **/
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Insured implements Serializable {
    private String name;
    private String idcard;
}
