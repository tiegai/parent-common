package com.nike.ncp.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }
}
