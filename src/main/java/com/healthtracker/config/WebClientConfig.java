package com.healthtracker.config;

import com.healthtracker.infrastructure.interceptor.MetricsInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * WebClient configuration with metrics interceptor.
 */
@Configuration
public class WebClientConfig {

    private final MetricsInterceptor metricsInterceptor;

    public WebClientConfig(MetricsInterceptor metricsInterceptor) {
        this.metricsInterceptor = metricsInterceptor;
    }

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(metricsInterceptor.captureMetrics())
                .build();
    }
}
