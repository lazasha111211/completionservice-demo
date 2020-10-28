package com.example.completionservicedemo.model;

import lombok.*;

import java.io.Serializable;

/**
 * @description: TODO
 * @author: lazasha
 * @date: 2020/1/17  11:07
 **/
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestModel<T extends Object> implements Serializable {
    private String key;
    private T data;
}
