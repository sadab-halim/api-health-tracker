package com.healthtracker.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

/**
 * Demo client that makes external API calls for testing.
 */
@Service
public class DemoApiClient {

    private static final Logger log = LoggerFactory.getLogger(DemoApiClient.class);

    private final WebClient webClient;
    private final Random random = new Random();

    public DemoApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Call httpbin.org (always successful).
     */
    public Mono<String> callHttpBin() {
        return webClient.get()
                .uri("https://httpbin.org/status/200")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(error -> {
                    log.error("Error calling httpbin.org", error);
                    return Mono.just("Error: " + error.getMessage());
                });
    }

    /**
     * Call httpbin.org with error status.
     */
    public Mono<String> callHttpBinError() {
        int errorCode = random.nextBoolean() ? 500 : 503;

        return webClient.get()
                .uri("https://httpbin.org/status/" + errorCode)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(error -> {
                    log.debug("Expected error from httpbin: {}", error.getMessage());
                    return Mono.just("Expected error: " + errorCode);
                });
    }

    /**
     * Call with random delay.
     */
    public Mono<String> callWithDelay() {
        int delaySeconds = random.nextInt(3) + 1; // 1-3 seconds

        return webClient.get()
                .uri("https://httpbin.org/delay/" + delaySeconds)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(error -> Mono.just("Timeout"));
    }

    /**
     * Make random API call (mix of success/error).
     */
    public Mono<String> makeRandomCall() {
        double rand = random.nextDouble();

        if (rand < 0.7) {
            // 70% success
            return callHttpBin();
        } else if (rand < 0.9) {
            // 20% errors
            return callHttpBinError();
        } else {
            // 10% slow requests
            return callWithDelay();
        }
    }
}
