package com.example.completionservicedemo.service;

import com.example.completionservicedemo.model.ExternalCallResultModel;
import com.example.completionservicedemo.model.Insured;
import com.example.completionservicedemo.model.PolicyModel;
import com.example.completionservicedemo.model.TaskResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @description: 外网接口调用，比较耗时
 * @author: lazasha
 * @date: 2020/1/17  10:49
 **/
@Service
public class ExternalCallServiceImpl implements ExternalCallService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalCallServiceImpl.class);
    @Override
    public TaskResponseModel<Object> externalCall(String key, Insured insured) {
        try {
            //假设耗时200ms
            Thread.sleep(200);
            TaskResponseModel<Object> taskResponseModel = new TaskResponseModel<>();
            taskResponseModel.setKey(key);
            taskResponseModel.setResultCode("200");
            ExternalCallResultModel externalCallResultModel = new ExternalCallResultModel();
            externalCallResultModel.setIdcard(insured.getIdcard());
            externalCallResultModel.setScore(200);
            taskResponseModel.setData(externalCallResultModel);
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
