package com.nike.ncp.common.executor.configuration;

import com.nike.wingtips.spring.webflux.client.WingtipsSpringWebfluxExchangeFilterFunction;
import com.nike.wingtips.spring.webflux.server.WingtipsSpringWebfluxWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebFluxConfiguration {
    @Bean
    public WingtipsSpringWebfluxWebFilter wingtipsSpringWebfluxWebFilter() {
        return WingtipsSpringWebfluxWebFilter
                .newBuilder()
                .build();
    }

    @Bean(name = "commonExecutorWebClient")
    public WebClient commonExecutorWebClient() {
        return WebClient.builder()
                /*
                  traceId outbound: automatic sub-span and tracing propagation to downstream
                  https://github.com/Nike-Inc/wingtips/tree/main/wingtips-spring-webflux#register-a-wingtipsspringwebfluxexchangefilterfunction-for-automatic-tracing-propagation-when-using-springs-reactive-webclient
                 */
                .filter(WingtipsSpringWebfluxExchangeFilterFunction.DEFAULT_IMPL)
                .build();
    }
}
