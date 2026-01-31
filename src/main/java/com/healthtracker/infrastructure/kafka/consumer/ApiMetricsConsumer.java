package com.healthtracker.infrastructure.kafka.consumer;

import com.healthtracker.infrastructure.kafka.producer.ApiMetricEvent;
import com.healthtracker.infrastructure.persistence.batch.BatchWriter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ApiMetricsConsumer {

    private static final Logger log = LoggerFactory.getLogger(ApiMetricsConsumer.class);

    private final BatchAccumulator batchAccumulator;
    private final BatchWriter batchWriter;
    private final AtomicLong messagesConsumed = new AtomicLong(0);

    public ApiMetricsConsumer(BatchAccumulator batchAccumulator, BatchWriter batchWriter) {
        this.batchAccumulator = batchAccumulator;
        this.batchWriter = batchWriter;
        log.info("ApiMetricsConsumer initialized");
    }

    /**
     * Kafka listener - consumes messages from api-metrics topic
     */
    @KafkaListener(
            topics = "api-metrics",
            groupId = "${spring.kafka.consumer.group-id}",  // ← Use property placeholder
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload ApiMetricEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            long count = messagesConsumed.incrementAndGet();

            log.info(">>> CONSUMED message {} from partition {} at offset {}: {}",
                    count, partition, offset, event.getApiName());

            // Add to batch accumulator
            List<ApiMetricEvent> batchToFlush = batchAccumulator.addEvent(partition, event);

            // If batch is ready, write to database
            if (batchToFlush != null && !batchToFlush.isEmpty()) {
                int written = batchWriter.writeBatch(batchToFlush);
                log.info(">>> FLUSHED batch: {} events written to database", written);
            }

            // Acknowledge the message (commit offset)
            acknowledgment.acknowledge();

            // Log progress every 10 messages
            if (count % 10 == 0) {
                log.info(">>> PROGRESS: Total messages consumed: {}, buffered events: {}",
                        count, batchAccumulator.getTotalBufferedEvents());
            }

        } catch (Exception e) {
            log.error(">>> ERROR processing message from partition {} at offset {}: {}",
                    partition, offset, e.getMessage(), e);
            // Still acknowledge to avoid reprocessing
            acknowledgment.acknowledge();
        }
    }

    /**
     * Get total messages consumed (for monitoring)
     */
    public long getMessagesConsumed() {
        return messagesConsumed.get();
    }
}
