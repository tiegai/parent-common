package com.nike.ncp.common.executor.configuration;

import com.nike.ncp.common.executor.controller.CommonActivityDispatchController;
import com.nike.ncp.common.executor.controller.CustomActivityDispatchController;
import com.nike.ncp.common.executor.service.CommonActivityDispatchService;
import com.nike.ncp.common.executor.service.CustomActivityDispatchService;
import com.nike.ncp.common.executor.service.ProxyFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ActivityDispatchControllerConfiguration<CHECKED_DATA> {
    @Autowired(required = false)
    private transient CommonActivityDispatchService<CHECKED_DATA> activityDispatchService;
    @Autowired(required = false)
    private transient CustomActivityDispatchService<CHECKED_DATA> customActivityDispatchService;
    @Resource
    private transient ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private transient ProxyFeedbackService proxyFeedbackService;

    @Bean
    @ConditionalOnBean(CommonActivityDispatchService.class)
    public <ACTIVITY_CONFIG> CommonActivityDispatchController<ACTIVITY_CONFIG, CHECKED_DATA> activityDispatchController() {
        return new CommonActivityDispatchController<>(threadPoolExecutor, activityDispatchService, proxyFeedbackService);
    }

    @Bean
    @ConditionalOnBean(CustomActivityDispatchService.class)
    public <ACTIVITY_CONFIG> CustomActivityDispatchController<ACTIVITY_CONFIG, CHECKED_DATA> customActivityDispatchController() {
        return new CustomActivityDispatchController<>(threadPoolExecutor, customActivityDispatchService);
    }
}

