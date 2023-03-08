package com.nike.ncp.common.executor.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorThreadPoolConfiguration {

    @Value("${ncp.executor.thread.count}")
    private Integer treadPoolSize;

    @Bean("activityTreadPoolExecutor")
    public ThreadPoolExecutor threadPoolExecutor() {

        return new ThreadPoolExecutor(
                Objects.requireNonNullElse(treadPoolSize, 2),
                Objects.requireNonNullElse(treadPoolSize, 2),
                0,
                TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
