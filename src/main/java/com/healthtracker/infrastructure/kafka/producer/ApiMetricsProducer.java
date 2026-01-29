package com.healthtracker.infrastructure.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ApiMetricsProducer {

    private static final Logger log = LoggerFactory.getLogger(ApiMetricsProducer.class);
    private static final String TOPIC = "api-metrics";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ApiMetricsProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send API metric event to Kafka asynchronously
     *
     * @param event The API metric event to send
     */
    public void sendMetric(ApiMetricEvent event) {
        // Use api_name as partition key for consistent partitioning
        String key = event.getApiName();

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent metric for {} to partition {} with offset {}",
                        event.getApiName(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send metric for {}: {}",
                        event.getApiName(),
                        ex.getMessage());
            }
        });
    }

    /**
     * Send metric synchronously (for testing)
     *
     * @param event The API metric event to send
     * @return SendResult with metadata
     */
    public SendResult<String, Object> sendMetricSync(ApiMetricEvent event) {
        try {
            String key = event.getApiName();
            SendResult<String, Object> result = kafkaTemplate.send(TOPIC, key, event).get();
            log.info("Sent metric synchronously for {} to partition {} with offset {}",
                    event.getApiName(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            return result;
        } catch (Exception e) {
            log.error("Failed to send metric synchronously: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send metric to Kafka", e);
        }
    }
}
