package com.healthtracker.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo controller for generating test traffic.
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    private final DemoApiClient demoApiClient;

    public DemoController(DemoApiClient demoApiClient) {
        this.demoApiClient = demoApiClient;
    }

    /**
     * Generate random API load (captured by interceptor).
     */
    @GetMapping("/generate-load")
    public Mono<Map<String, Object>> generateLoad() {
        log.debug("Generating test API call");

        return demoApiClient.makeRandomCall()
                .map(result -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "completed");
                    response.put("result", result);
                    return response;
                })
                .onErrorResume(error -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("error", error.getMessage());
                    return Mono.just(response);
                });
    }

    /**
     * Test successful API call.
     */
    @GetMapping("/test-success")
    public Mono<Map<String, String>> testSuccess() {
        return demoApiClient.callHttpBin()
                .map(result -> Map.of("status", "success", "result", result));
    }

    /**
     * Test failed API call.
     */
    @GetMapping("/test-failure")
    public Mono<Map<String, String>> testFailure() {
        return demoApiClient.callHttpBinError()
                .map(result -> Map.of("status", "error", "result", result));
    }

    /**
     * Test slow API call.
     */
    @GetMapping("/test-slow")
    public Mono<Map<String, String>> testSlow() {
        return demoApiClient.callWithDelay()
                .map(result -> Map.of("status", "slow", "result", result));
    }
}
