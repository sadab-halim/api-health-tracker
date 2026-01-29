package com.healthtracker.api.controller;

import com.healthtracker.infrastructure.kafka.producer.ApiMetricEvent;
import com.healthtracker.infrastructure.kafka.producer.ApiMetricsProducer;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class MetricsTestController {

    private final ApiMetricsProducer metricsProducer;

    public MetricsTestController(ApiMetricsProducer metricsProducer) {
        this.metricsProducer = metricsProducer;
    }

    /**
     * Test endpoint to send a sample metric to Kafka
     */
    @PostMapping("/send-metric")
    public Map<String, Object> sendTestMetric(
            @RequestParam(defaultValue = "stripe_api") String apiName,
            @RequestParam(defaultValue = "200") Integer statusCode,
            @RequestParam(defaultValue = "150") Integer latencyMs) {

        ApiMetricEvent event = ApiMetricEvent.builder()
                .apiName(apiName)
                .endpoint("/v1/charges")
                .method("POST")
                .statusCode(statusCode)
                .latencyMs(latencyMs)
                .timestamp(Instant.now())
                .errorMessage(statusCode >= 400 ? "Error occurred" : null)
                .metadata(Map.of(
                        "service_name", "test-service",
                        "environment", "development"
                ))
                .build();

        metricsProducer.sendMetric(event);

        return Map.of(
                "status", "sent",
                "eventId", event.getEventId(),
                "apiName", event.getApiName(),
                "statusCode", event.getStatusCode(),
                "message", "Metric sent to Kafka topic: api-metrics"
        );
    }

    /**
     * Send multiple test metrics at once
     */
    @PostMapping("/send-batch")
    public Map<String, Object> sendBatchMetrics(@RequestParam(defaultValue = "10") int count) {
        String[] apis = {"stripe_api", "twilio_api", "google_maps_api", "auth0_api"};
        int[] statusCodes = {200, 201, 400, 429, 500, 503};

        for (int i = 0; i < count; i++) {
            String apiName = apis[i % apis.length];
            int statusCode = statusCodes[i % statusCodes.length];
            int latency = 50 + (i * 10);

            ApiMetricEvent event = ApiMetricEvent.builder()
                    .apiName(apiName)
                    .endpoint("/test/endpoint/" + i)
                    .method("GET")
                    .statusCode(statusCode)
                    .latencyMs(latency)
                    .timestamp(Instant.now())
                    .errorMessage(statusCode >= 400 ? "Test error" : null)
                    .build();

            metricsProducer.sendMetric(event);
        }

        return Map.of(
                "status", "sent",
                "count", count,
                "message", count + " metrics sent to Kafka"
        );
    }
}
