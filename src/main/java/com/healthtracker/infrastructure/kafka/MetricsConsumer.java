package com.healthtracker.infrastructure.kafka;

import com.healthtracker.domain.ApiMetric;
import com.healthtracker.infrastructure.persistence.MetricsBatchWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Kafka consumer for API metrics.
 * Processes messages in batches for efficiency.
 */
@Service
public class MetricsConsumer {

    private static final Logger log = LoggerFactory.getLogger(MetricsConsumer.class);

    private final MetricsBatchWriter batchWriter;

    public MetricsConsumer(MetricsBatchWriter batchWriter) {
        this.batchWriter = batchWriter;
    }

    /**
     * Consume metrics from Kafka and write to database in batches.
     */
    @KafkaListener(
            topics = "${kafka.topics.api-metrics.name}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMetrics(List<ApiMetric> metrics, Acknowledgment acknowledgment) {
        try {
            log.debug("Received batch of {} metrics", metrics.size());

            // Write batch to database
            int saved = batchWriter.saveBatch(metrics);

            // Commit offset after successful write
            acknowledgment.acknowledge();

            log.info("Processed batch: {} metrics written to database", saved);

        } catch (Exception e) {
            log.error("Error processing batch of {} metrics", metrics.size(), e);
            // Don't acknowledge - will be retried
            throw e;
        }
    }
}
