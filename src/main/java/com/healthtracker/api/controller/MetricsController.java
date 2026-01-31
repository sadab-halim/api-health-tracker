package com.healthtracker.api.controller;

import com.healthtracker.api.dto.*;
import com.healthtracker.application.service.MetricsAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MetricsController {

    private final MetricsAnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<MetricsSummaryDTO> getSummary(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(analyticsService.getSummary(hours));
    }

    @GetMapping("/by-api")
    public ResponseEntity<List<ApiHealthDTO>> getApiHealth(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(analyticsService.getApiHealthStats(hours));
    }

    @GetMapping("/by-endpoint")
    public ResponseEntity<List<EndpointMetricsDTO>> getEndpointMetrics(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(analyticsService.getEndpointMetrics(hours));
    }

    @GetMapping("/time-series")
    public ResponseEntity<List<TimeSeriesDataDTO>> getTimeSeries(
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(analyticsService.getTimeSeriesData(hours));
    }

    @GetMapping("/errors")
    public ResponseEntity<List<ErrorDetailsDTO>> getRecentErrors(
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(analyticsService.getRecentErrors(hours, limit));
    }
}
