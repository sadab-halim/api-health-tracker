package com.healthtracker.infrastructure.kafka;

import com.healthtracker.domain.ApiMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for producing API metrics to Kafka.
 */
@Service
public class MetricsProducer {

    private static final Logger log = LoggerFactory.getLogger(MetricsProducer.class);

    private final KafkaTemplate<String, ApiMetric> kafkaTemplate;

    @Value("${kafka.topics.api-metrics.name:api-metrics}")
    private String topicName;

    public MetricsProducer(KafkaTemplate<String, ApiMetric> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send metric event to Kafka asynchronously.
     *
     * @param metric The API metric to send
     */
    public void sendMetric(ApiMetric metric) {
        try {
            // Use API name as partition key for better distribution
            String key = metric.getApiName();

            CompletableFuture<SendResult<String, ApiMetric>> future =
                    kafkaTemplate.send(topicName, key, metric);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Sent metric: api={}, status={}, partition={}, offset={}",
                            metric.getApiName(),
                            metric.getStatusCode(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send metric: api={}, error={}",
                            metric.getApiName(), ex.getMessage());
                }
            });

        } catch (Exception e) {
            log.error("Error sending metric to Kafka", e);
        }
    }

    /**
     * Send metric synchronously (for testing).
     */
    public void sendMetricSync(ApiMetric metric) {
        try {
            String key = metric.getApiName();
            SendResult<String, ApiMetric> result = kafkaTemplate.send(topicName, key, metric).get();

            log.info("Sent metric synchronously: api={}, partition={}, offset={}",
                    metric.getApiName(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

        } catch (Exception e) {
            log.error("Error sending metric synchronously", e);
            throw new RuntimeException("Failed to send metric", e);
        }
    }
}
