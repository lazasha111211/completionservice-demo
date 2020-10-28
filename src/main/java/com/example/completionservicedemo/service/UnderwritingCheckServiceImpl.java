package com.example.completionservicedemo.service;

import com.example.completionservicedemo.model.Insured;
import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.TaskResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @description: 核保校验
 * @author: lazasha
 * @date: 2020/1/17  10:41
 **/
@Service
public class UnderwritingCheckServiceImpl implements UnderwritingCheckService {
    private static final Logger logger = LoggerFactory.getLogger(UnderwritingCheckServiceImpl.class);
    @Override
    public TaskResponseModel<Object> underwritingCheck(String key, PolicyModel policyModel) {
        try {
            //假设耗时50ms
            Thread.sleep(50);
            TaskResponseModel<Object> taskResponseModel = new TaskResponseModel<>();
            taskResponseModel.setKey(key);
            taskResponseModel.setResultCode("200");
            taskResponseModel.setData(policyModel);
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
