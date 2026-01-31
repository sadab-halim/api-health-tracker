package com.healthtracker.api.controller;

import com.healthtracker.infrastructure.kafka.consumer.ApiMetricsConsumer;
import com.healthtracker.infrastructure.kafka.consumer.BatchAccumulator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final ApiMetricsConsumer apiMetricsConsumer;
    private final BatchAccumulator batchAccumulator;

    public HealthController(ApiMetricsConsumer apiMetricsConsumer, BatchAccumulator batchAccumulator) {
        this.apiMetricsConsumer = apiMetricsConsumer;
        this.batchAccumulator = batchAccumulator;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "timestamp", Instant.now(),
                "application", "API Health Tracker"
        );
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "name", "API Health Tracker",
                "version", "1.0.0",
                "description", "Third-Party API Health Monitoring System"
        );
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "messagesConsumed", apiMetricsConsumer.getMessagesConsumed(),
                "bufferedEvents", batchAccumulator.getTotalBufferedEvents(),
                "timestamp", Instant.now()
        );
    }
}
