package com.example.completionservicedemo.task;

import com.example.completionservicedemo.model.TaskResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @description: 任务执行
 * @author: lazasha
 * @date: 2020/1/17  9:32
 **/
@Component
public class TaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(TaskExecutor.class);
    //线程池
    private final ExecutorService executorService;

    public TaskExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    //异步执行，获取所有结果后返回
    public List<TaskResponseModel<Object>> execute(List<Callable<TaskResponseModel<Object>>> commands) {
        //创建异步执行对象
        CompletionService<TaskResponseModel<Object>> completionService = new ExecutorCompletionService<>(executorService);
        for (Callable<TaskResponseModel<Object>> command : commands) {
            completionService.submit(command);
        }
        //获取所有异步执行线程的结果
        int taskCount = commands.size();
        List<TaskResponseModel<Object>> params = new ArrayList<>(taskCount);
        try {
            for (int i = 0; i < taskCount; i++) {
                Future<TaskResponseModel<Object>> future = completionService.take();
                params.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            //异常处理
            TaskResponseModel<Object> taskResponseModel = new TaskResponseModel<>();
            taskResponseModel.setKey("error");
            taskResponseModel.setResultCode("400");
            taskResponseModel.setResultMessage("异步执行线程错误");
            taskResponseModel.setData(null);
            params.clear();
            params.add(taskResponseModel);
            logger.error(e.getMessage());
        }
        //返回，如果执行中发生error, 则返回相应的code： 5000
        return params;
    }
}
