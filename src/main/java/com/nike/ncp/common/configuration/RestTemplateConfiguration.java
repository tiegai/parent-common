package com.nike.ncp.common.configuration;

import com.nike.wingtips.spring.interceptor.WingtipsClientHttpRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new WingtipsClientHttpRequestInterceptor());
        return restTemplate;
    }
}
