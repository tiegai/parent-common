package com.nike.ncp.common.executor.controller;

import com.nike.ncp.common.executor.properties.CommonExecutorProperties;
import com.nike.ncp.common.executor.service.EcsScaleOutService;
import com.nike.wingtips.util.AsyncWingtipsHelperStatic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;

import static com.nike.ncp.common.utilities.configuration.AppNameConfiguration.APP_NAME_BEAN_NAME;

@Slf4j
@RestController
@RequestMapping("/v1")
public class EcsScaleOutControllerV1 {
    @Resource
    private CommonExecutorProperties properties;
    @Resource
    @Qualifier(value = APP_NAME_BEAN_NAME)
    private String appName;
    @Lazy
    @Resource
    private EcsScaleOutService ecsScaleOutService;

    @PutMapping("/ecs/desired-count")
    public ResponseEntity<String> scaleOut() {
        if (null == properties.getEcsClusterName() || null == appName) {
            log.warn("Scaling not enabled.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Scaling not enabled.");
        }

        // https://stackoverflow.com/questions/69169969/void-vs-monovoid-in-spring-webflux#comment122268553_69169969
        Mono.fromRunnable(
                AsyncWingtipsHelperStatic.runnableWithTracing(
                        () -> ecsScaleOutService.scaleOut()
                )
            ).subscribeOn(Schedulers.single())
            .subscribe();

        return ResponseEntity.accepted().build();
    }
}
