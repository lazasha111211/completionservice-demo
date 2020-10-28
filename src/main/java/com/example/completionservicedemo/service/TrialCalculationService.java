package com.example.completionservicedemo.service;

import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.Risk;
import com.example.completionservicedemo.model.TaskResponseModel;

/**
 * @description: TODO
 * @author: lazasha
 * @date: 2020/1/17  10:45
 **/

public interface TrialCalculationService {
    TaskResponseModel<Object> trialCalc(String key, Risk risk);
}
