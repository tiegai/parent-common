package com.nike.ncp.common.executor.service;

import com.nike.ncp.common.executor.properties.CommonExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.endpoints.EcsEndpointProvider;
import software.amazon.awssdk.services.ecs.model.Service;
import software.amazon.awssdk.services.ecs.model.UpdateServiceResponse;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.nike.ncp.common.utilities.configuration.AppNameConfiguration.APP_NAME_BEAN_NAME;

@Slf4j
@Lazy
@org.springframework.stereotype.Service
public class EcsScaleOutService {
    @Resource
    private CommonExecutorProperties properties;
    @Resource
    @Qualifier(value = APP_NAME_BEAN_NAME)
    private String appName;

    public void scaleOut() {
        final String ecsClusterName = properties.getEcsClusterName();
        final String ecsServiceName = appName;
        if (null == ecsClusterName || null == ecsServiceName) {
            return; // TODO throw
        }

        EcsClient ecs = EcsClient.builder().endpointProvider(EcsEndpointProvider.defaultProvider()).build();
        int currentCount = getCurrentCount(ecs);
        int desiredCount = currentCount + 1;
        updateEcsService(ecs, currentCount, desiredCount);
    }

    private int getCurrentCount(EcsClient ecs) {
        final String ecsClusterName = properties.getEcsClusterName();
        final String ecsServiceName = appName;

        List<Service> services = ecs.describeServices(builder -> builder
                .cluster(ecsClusterName)
                .services(ecsServiceName)
        ).services();

        if (services.size() > 1) {
            throw new IllegalStateException(
                    String.format("Expected only 1 but actually found %d ECS services: ecsServiceName=%s, ecsClusterName=%s",
                            services.size(), ecsServiceName, ecsClusterName));
        }

        return services.stream()
                .filter(s -> Objects.equals(ecsServiceName, s.serviceName()))
                .map(Service::desiredCount)
                .findAny()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("ECS service not found: ecsServiceName=%s, ecsClusterName=%s",
                                ecsServiceName, ecsClusterName)));
    }

    private void updateEcsService(EcsClient ecs, int current, int desired) {
        final String ecsClusterName = properties.getEcsClusterName();
        final String ecsServiceName = appName;

        UpdateServiceResponse updated = ecs.updateService(builder -> builder
                .cluster(ecsClusterName)
                .service(ecsServiceName)
                .desiredCount(desired)
        );

        final boolean successful = updated.sdkHttpResponse().isSuccessful();
        if (successful) {
            log.info("Successful scale-out ( before={}, after={} ): ecsServiceName={}, ecsClusterName={}, requestId={}",
                    current, desired, ecsServiceName, ecsClusterName, updated.responseMetadata().requestId());
        } else {
            log.error("Failed scale-out ( before={}, after={} ): ecsServiceName={}, ecsClusterName={}, requestId={}",
                    current, desired, ecsServiceName, ecsClusterName, updated.responseMetadata().requestId());
        }
    }
}
