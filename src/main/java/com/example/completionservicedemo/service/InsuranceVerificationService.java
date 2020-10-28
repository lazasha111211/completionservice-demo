package com.example.completionservicedemo.service;

import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.TaskResponseModel;

/**
 * @description: TODO
 * @author: lazasha
 * @date: 2020/1/17  10:21
 **/

public interface InsuranceVerificationService {
    TaskResponseModel<Object> insuranceCheck(String key, PolicyModel policyModel);
}
