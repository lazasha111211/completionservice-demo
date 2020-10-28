package com.example.completionservicedemo.callback;

import com.example.completionservicedemo.model.Insured;
import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.TaskResponseModel;
import com.example.completionservicedemo.service.UnderwritingCheckService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Callable;

/**
 * @description: 核保校验线程
 * @author: lazasha
 * @date: 2020/1/17  11:07
 **/
@Data
@AllArgsConstructor
public class UnderwritingCheckCallback implements Callable<TaskResponseModel<Object>> {
    private String key;
    private PolicyModel policyModel;
    private final UnderwritingCheckService underwritingCheckService;
    @Override
    public TaskResponseModel<Object> call() throws Exception {
        return underwritingCheckService.underwritingCheck(key, policyModel);
    }
}
