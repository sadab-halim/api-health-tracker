package com.healthtracker.infrastructure.interceptor;

import com.healthtracker.domain.ApiMetric;
import com.healthtracker.infrastructure.kafka.MetricsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.UUID;

/**
 * WebClient interceptor to capture API metrics.
 */
@Component
public class MetricsInterceptor {

    private static final Logger log = LoggerFactory.getLogger(MetricsInterceptor.class);
    private static final String START_TIME_KEY = "startTime";
    private static final String REQUEST_ID_KEY = "requestId";

    private final MetricsProducer metricsProducer;

    public MetricsInterceptor(MetricsProducer metricsProducer) {
        this.metricsProducer = metricsProducer;
    }

    /**
     * Create exchange filter function to capture metrics.
     */
    public ExchangeFilterFunction captureMetrics() {
        return (request, next) -> {
            long startTime = System.currentTimeMillis();
            String requestId = UUID.randomUUID().toString();
            String apiName = request.url().getHost().replace(".", "_") + "_api";
            String endpoint = request.url().getPath();
            String method = request.method().name();

            return next.exchange(request)
                    .doOnNext(response -> {
                        try {
                            long latencyMs = System.currentTimeMillis() - startTime;
                            int statusCode = response.statusCode().value();

                            // Build metric
                            ApiMetric metric = ApiMetric.builder()
                                    .apiName(apiName)
                                    .endpoint(endpoint)
                                    .method(method)
                                    .statusCode(statusCode)
                                    .latencyMs((int) latencyMs)
                                    .timestamp(Instant.now())
                                    .requestId(requestId)
                                    .serviceName("api-health-tracker")
                                    .build();

                            // Add error message for failed requests
                            if (response.statusCode().isError()) {
                                metric.setErrorMessage(response.statusCode().toString());
                            }

                            // Send to Kafka asynchronously
                            metricsProducer.sendMetric(metric);

                            log.debug("Captured metric: api={}, status={}, latency={}ms",
                                    apiName, statusCode, latencyMs);

                        } catch (Exception e) {
                            log.error("Error capturing metric", e);
                        }
                    })
                    .doOnError(error -> {
                        try {
                            long latencyMs = System.currentTimeMillis() - startTime;

                            // Build metric for failed request
                            ApiMetric metric = ApiMetric.builder()
                                    .apiName(apiName)
                                    .endpoint(endpoint)
                                    .method(method)
                                    .statusCode(0) // Network error
                                    .latencyMs((int) latencyMs)
                                    .timestamp(Instant.now())
                                    .requestId(requestId)
                                    .serviceName("api-health-tracker")
                                    .errorMessage(error.getMessage())
                                    .build();

                            metricsProducer.sendMetric(metric);

                            log.debug("Captured error metric: api={}, error={}", apiName, error.getMessage());

                        } catch (Exception e) {
                            log.error("Error capturing error metric", e);
                        }
                    });
        };
    }
}
