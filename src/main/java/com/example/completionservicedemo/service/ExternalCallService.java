package com.example.completionservicedemo.service;

import com.example.completionservicedemo.model.Insured;
import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.TaskResponseModel;

/**
 * @description: TODO
 * @author: lazasha
 * @date: 2020/1/17  10:48
 **/

public interface ExternalCallService {
    TaskResponseModel<Object> externalCall(String key, Insured insured);
}
