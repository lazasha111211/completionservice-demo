package com.example.completionservicedemo.service;

import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.Risk;
import com.example.completionservicedemo.model.TaskResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description: 保费试算
 * @author: lazasha
 * @date: 2020/1/17  10:46
 **/
@Service
public class TrialCalculationServiceImpl implements TrialCalculationService {
    private static final Logger logger = LoggerFactory.getLogger(TrialCalculationServiceImpl.class);
    @Override
    public TaskResponseModel<Object> trialCalc(String key, Risk risk) {
        try {
            //假设耗时50ms
            Thread.sleep(50);
            TaskResponseModel<Object> taskResponseModel = new TaskResponseModel<>();
            taskResponseModel.setKey(key);
            taskResponseModel.setResultCode("200");
            taskResponseModel.setData(risk);
            return taskResponseModel;
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
            TaskResponseModel<Object> taskResponseModel = new TaskResponseModel<>();
            taskResponseModel.setKey(key);
            taskResponseModel.setResultCode("400");
            taskResponseModel.setResultMessage(e.getMessage());
            return taskResponseModel;

        }

    }
}
