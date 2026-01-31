package com.healthtracker.infrastructure.persistence.batch;

import com.healthtracker.domain.model.ApiMetric;
import com.healthtracker.domain.repository.ApiMetricRepository;
import com.healthtracker.infrastructure.kafka.producer.ApiMetricEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchWriter {

    private static final Logger log = LoggerFactory.getLogger(BatchWriter.class);

    private final ApiMetricRepository apiMetricRepository;

    public BatchWriter(ApiMetricRepository apiMetricRepository) {
        this.apiMetricRepository = apiMetricRepository;
    }

    /**
     * Write batch of events to database
     * Returns number of successfully written records
     */
    @Transactional
    public int writeBatch(List<ApiMetricEvent> events) {
        if (events == null || events.isEmpty()) {
            return 0;
        }

        log.info("Writing batch of {} events to database", events.size());

        List<ApiMetric> metrics = new ArrayList<>();
        int successCount = 0;
        int duplicateCount = 0;

        for (ApiMetricEvent event : events) {
            try {
                ApiMetric metric = convertToEntity(event);
                metrics.add(metric);
            } catch (Exception e) {
                log.error("Failed to convert event to entity: {}", e.getMessage());
            }
        }

        // Batch save all metrics
        try {
            List<ApiMetric> saved = apiMetricRepository.saveAll(metrics);
            successCount = saved.size();
            log.info("Successfully wrote {} metrics to database", successCount);
        } catch (DataIntegrityViolationException e) {
            // Handle constraint violations (duplicates)
            log.warn("Constraint violation in batch, saving individually to identify duplicates");

            // Save one by one to identify duplicates
            for (ApiMetric metric : metrics) {
                try {
                    apiMetricRepository.save(metric);
                    successCount++;
                } catch (DataIntegrityViolationException dupEx) {
                    duplicateCount++;
                    log.debug("Duplicate metric skipped: {}", metric.getEventId());
                }
            }

            log.info("Batch complete: {} saved, {} duplicates skipped", successCount, duplicateCount);
        }

        return successCount;
    }

    /**
     * Convert Kafka event to JPA entity
     */
    private ApiMetric convertToEntity(ApiMetricEvent event) {
        return ApiMetric.builder()
                .eventId(event.getEventId())
                .apiName(event.getApiName())
                .endpoint(event.getEndpoint())
                .method(event.getMethod())
                .statusCode(event.getStatusCode())
                .latencyMs(event.getLatencyMs())
                .timestamp(event.getTimestamp())
                .errorMessage(event.getErrorMessage())
                .metadata(event.getMetadata())
                .build();
    }
}
