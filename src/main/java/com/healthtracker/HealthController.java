package com.healthtracker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("application", "API Health Tracker");
        response.put("status", "Running");
        response.put("version", "1.0.0");
        return response;
    }

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }
}
