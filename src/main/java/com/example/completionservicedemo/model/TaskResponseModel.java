package com.example.completionservicedemo.model;

import lombok.*;

import javax.annotation.sql.DataSourceDefinition;
import java.io.Serializable;

/**
 * @description: 异步执行返回的信息
 * @author: lazasha
 * @date: 2020/1/17  9:33
 **/
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskResponseModel<T extends Object> implements Serializable {
    private String key;
    private String resultCode;
    private String resultMessage;
    private T data;
}
