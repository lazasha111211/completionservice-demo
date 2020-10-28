package com.example.completionservicedemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: 自定义线程池
 * @author: lazasha
 * @date: 2020/1/17  9:48
 **/
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService getThreadPool(){

        /*          核心线程池的大小是：3
         *          线程池的最大数量：5
         *          空闲线程的存活时间：2000
         *          空闲线程的存活时间的单位：ms
         *          工作队列：基于链表结构的阻塞队列
         *          饱和策略：没有声明默认采用CallerRunsPolicy （由调用线程处理该任务）
         */
        return new ThreadPoolExecutor(75,
                125,
                180000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(450),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
