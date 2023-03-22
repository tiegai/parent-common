package com.nike.ncp.common.executor.service;

import com.nike.internal.util.StringUtils;
import com.nike.ncp.common.executor.properties.CommonExecutorProperties;
import com.nike.ncp.common.executor.properties.EcsEnvMetaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.endpoints.EcsEndpointProvider;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
@EnableScheduling
public class EcsScaleInProtectionService implements SchedulingConfigurer {

    @Resource
    private CommonExecutorProperties properties;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    private final String defaultPro = "local";
    private final String sysProfileKey = "spring.profiles.active";

    public void scaleInProtection(String runType, boolean protectionEnabled) {
        String sysPro = System.getProperty(sysProfileKey, defaultPro);
        if (defaultPro.equalsIgnoreCase(sysPro)) {
            return;
        }
        final String ecsClusterName = properties.getEcsClusterName();
        if (StringUtils.isBlank(ecsClusterName)) {
            throw new UnsupportedOperationException("Cluster name is required");
        }
        EcsClient ecs = EcsClient.builder().endpointProvider(EcsEndpointProvider.defaultProvider()).build();
        boolean hasProtected = ecs.updateTaskProtection(builder -> builder
                .cluster(ecsClusterName)
                .protectionEnabled(protectionEnabled)
                .tasks(EcsEnvMetaData.getContainerARN())
        ).hasProtectedTasks();
        if (!hasProtected) {
            throw new IllegalStateException(
                    String.format("%s scale-in protection as failure ECS services: ecsClusterName=%s, taskArn=%s",
                            runType, ecsClusterName, EcsEnvMetaData.getContainerARN()));
        }
        log.info("{} scale-in protection as success", runType);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(() -> scaleInScheduleCheck(),
                triggerContext -> new CronTrigger("0 0 0/1 * * ?").nextExecutionTime(triggerContext));
    }

    private void scaleInScheduleCheck() {
        if (threadPoolExecutor.getActiveCount() == 0) {
            scaleInProtection("Disable", false);
            return;
        }
        scaleInProtection("Renew", true);
    }
}
