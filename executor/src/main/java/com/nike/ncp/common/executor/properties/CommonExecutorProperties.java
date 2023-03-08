package com.nike.ncp.common.executor.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = {
    "classpath:/application.properties",
    "classpath:/application-${spring.profiles.active}.properties",
    "classpath:/common-executor.properties",
    "classpath:/common-executor-${spring.profiles.active}.properties"
}, ignoreResourceNotFound = true)
@SuppressWarnings("SpringFacetCodeInspection")
public class CommonExecutorProperties {
    /**
     * e.g. http(s)://internal-onencp-proxy-alb-428960147.cn-northwest-1.elb.amazonaws.com.cn <br/>
     *      &emsp;&emsp;http(s)://proxy.onencp-test.gcncp.nikecloud.com.cn
     */
    @Value("${ncp.proxy.host.url}")
    private String proxyHostUrl;
    @Value("${ncp.proxy.success.feedback.path}")
    private String successFeedbackPath;
    @Value("${ncp.proxy.failure.feedback.path}")
    private String failureFeedbackPath;

    @Value("${ecs.cluster.name:#{null}}")
    private String ecsClusterName;
}
