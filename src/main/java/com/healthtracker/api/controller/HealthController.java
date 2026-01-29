package com.healthtracker.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

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
}
