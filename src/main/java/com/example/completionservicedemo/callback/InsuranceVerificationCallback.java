package com.example.completionservicedemo.callback;

import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.TaskResponseModel;
import com.example.completionservicedemo.service.InsuranceVerificationService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Callable;

/**
 * @description: 投保校验线程
 * @author: lazasha
 * @date: 2020/1/17  11:05
 **/
@Data
@AllArgsConstructor
public class InsuranceVerificationCallback implements Callable<TaskResponseModel<Object>> {
    private String key;
    private PolicyModel policyModel;
    private final InsuranceVerificationService insuranceVerificationService;
    @Override
    public TaskResponseModel<Object> call() throws Exception {
        return insuranceVerificationService.insuranceCheck(key, policyModel);
    }
}
