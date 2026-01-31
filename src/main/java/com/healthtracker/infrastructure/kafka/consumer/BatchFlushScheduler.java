package com.healthtracker.infrastructure.kafka.consumer;

import com.healthtracker.infrastructure.kafka.producer.ApiMetricEvent;
import com.healthtracker.infrastructure.persistence.batch.BatchWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class BatchFlushScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchFlushScheduler.class);

    private final BatchAccumulator batchAccumulator;
    private final BatchWriter batchWriter;

    public BatchFlushScheduler(BatchAccumulator batchAccumulator, BatchWriter batchWriter) {
        this.batchAccumulator = batchAccumulator;
        this.batchWriter = batchWriter;
    }

    /**
     * Flush all batches every 5 seconds
     */
    @Scheduled(fixedRate = 5000)
    public void flushBatches() {
        try {
            List<List<ApiMetricEvent>> batches = batchAccumulator.flushAll();

            if (batches.isEmpty()) {
                log.debug("No batches to flush");
                return;
            }

            int totalWritten = 0;
            for (List<ApiMetricEvent> batch : batches) {
                int written = batchWriter.writeBatch(batch);
                totalWritten += written;
            }

            if (totalWritten > 0) {
                log.info("Scheduled flush completed: {} events written from {} batches",
                        totalWritten, batches.size());
            }

        } catch (Exception e) {
            log.error("Error during scheduled flush: {}", e.getMessage(), e);
        }
    }
}
