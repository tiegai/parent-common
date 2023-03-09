package com.nike.ncp.common.utilities.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
public class AppNameConfiguration {
    public static final String APP_NAME_BEAN_NAME = "appName";

    @Bean(name = APP_NAME_BEAN_NAME)
    public String appName(
            @Value("${app.name.key:#{null}}") String keyName,
            @Autowired Environment environment
    ) {
        String envVar = System.getenv(Objects.requireNonNullElse(keyName, "ONENCP_APP_NAME"));
        if (null != envVar) {
            return envVar;
        }

        String sysProp = System.getProperty(
                Objects.requireNonNullElse(keyName, "spring.application.name"),
                System.getProperty("info.app.name")
        );
        if (null != sysProp) {
            return sysProp;
        }

        return environment.getProperty(
                Objects.requireNonNullElse(keyName, "spring.application.name"),
                Objects.requireNonNullElse(environment.getProperty("info.app.name"), null)
        );
    }
}
