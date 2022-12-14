package com.nike.ncp.common.service;

import com.netflix.config.AbstractPollingScheduler;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PolledConfigurationSource;
import com.nike.cerberus.archaius.client.provider.CerberusConfigurationSource;
import com.nike.cerberus.client.CerberusClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ConfigurationService {

    @Value("${cerberus.config:}")
    private String configVaultPath;
    @Value("${cerberus.auth:}")
    private String authVaultPath;
    @Value("${cerberus.initialDelay:1200000}")
    private int cerberusInitialPollDelay;
    @Value("${cerberus.scheduleInterval:1200000}")
    private int cerberusScheduleInterval;

    private final CerberusClient cerberusClient;
    private final Environment environment;
    private ConcurrentCompositeConfiguration configInstance;

//    @PostConstruct
    protected void readValuesFromCerberus() {
        try {
            PolledConfigurationSource polledConfigurationSource = createPolledConfigurationSource();
            AbstractPollingScheduler abstractPollingScheduler = createAbstractPollingScheduler();
            DynamicConfiguration cerberusConfig = createCerberusConfig(polledConfigurationSource, abstractPollingScheduler);
            configInstance = createConfigInstance();
            configInstance.addConfiguration(cerberusConfig);
        } catch (Exception e) {
            log.error("event=load_cerberus_failed", e);
        }
    }

    ConcurrentCompositeConfiguration createConfigInstance() {
        return (ConcurrentCompositeConfiguration) ConfigurationManager.getConfigInstance();
    }

    DynamicConfiguration createCerberusConfig(PolledConfigurationSource polledConfigurationSource,
                                              AbstractPollingScheduler abstractPollingScheduler) {
        return new DynamicConfiguration(polledConfigurationSource, abstractPollingScheduler);
    }

    FixedDelayPollingScheduler createAbstractPollingScheduler() {
        return new FixedDelayPollingScheduler(cerberusInitialPollDelay, cerberusScheduleInterval, true);
    }

    CerberusConfigurationSource createPolledConfigurationSource() {
        return new CerberusConfigurationSource(cerberusClient, authVaultPath, configVaultPath);
    }

    public String getString(String propertyKey, String defaultValue) {
        String fromCerberus = configInstance.getString(propertyKey, "");
        return isNotBlank(fromCerberus) ? fromCerberus : environment.getProperty(propertyKey, defaultValue);
    }

    public String getString(String propertyKey) {
        return getString(propertyKey, null);
    }

    public int getInt(String propertyKey, int defaultValue) {
        Integer fromCerberus = configInstance.getInt(propertyKey, Integer.MIN_VALUE);
        return fromCerberus != Integer.MIN_VALUE ? fromCerberus
                : environment.getProperty(propertyKey, Integer.TYPE, defaultValue);
    }

    public String getConfigProperty(String propertyName) {
        String[] values = configInstance.getStringArray(propertyName);
        List<String> valueList = Arrays.stream(values).collect(Collectors.toList());
        if (valueList.isEmpty()) {
            return null;
        } else if (valueList.size() == 1) {
            return valueList.get(0);
        } else {
            StringBuilder valueBuilder = new StringBuilder();
            for (String val : valueList) {
                valueBuilder.append(val).append(",");
            }
            return valueBuilder.substring(0, valueBuilder.length() - 1);
        }
    }
}
