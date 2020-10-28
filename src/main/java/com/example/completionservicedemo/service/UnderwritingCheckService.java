package com.example.completionservicedemo.service;

import com.example.completionservicedemo.model.Insured;
import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.TaskResponseModel;

/**
 * @description: 核保校验
 * @author: lazasha
 * @date: 2020/1/17  10:34
 **/

public interface UnderwritingCheckService {
    TaskResponseModel<Object> underwritingCheck(String key, PolicyModel policyModel);
}
