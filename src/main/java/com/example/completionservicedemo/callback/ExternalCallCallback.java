package com.example.completionservicedemo.callback;

import com.example.completionservicedemo.model.Insured;
import com.example.completionservicedemo.model.Risk;
import com.example.completionservicedemo.model.TaskResponseModel;
import com.example.completionservicedemo.service.ExternalCallService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Callable;

/**
 * @description: 外网调用线程
 * @author: lazasha
 * @date: 2020/1/17  11:03
 **/
@Data
@AllArgsConstructor
public class ExternalCallCallback implements Callable<TaskResponseModel<Object>> {
    private String key;
    private Insured insured;
    private final ExternalCallService externalCallService;


    @Override
    public TaskResponseModel<Object> call() throws Exception {
        return externalCallService.externalCall(key, insured);
    }
}
