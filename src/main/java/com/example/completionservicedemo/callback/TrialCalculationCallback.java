package com.example.completionservicedemo.callback;

import com.example.completionservicedemo.model.Risk;
import com.example.completionservicedemo.model.TaskResponseModel;
import com.example.completionservicedemo.service.TrialCalculationService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Callable;

/**
 * @description: 保费试算线程
 * @author: lazasha
 * @date: 2020/1/17  11:05
 **/
@Data
@AllArgsConstructor
public class TrialCalculationCallback implements Callable<TaskResponseModel<Object>> {
    private String key;
    private Risk risk;
    private final TrialCalculationService trialCalculationService;
    @Override
    public TaskResponseModel<Object> call() throws Exception {
        return trialCalculationService.trialCalc(key, risk);
    }
}
