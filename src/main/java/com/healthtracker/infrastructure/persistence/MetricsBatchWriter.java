package com.healthtracker.infrastructure.persistence;

import com.healthtracker.domain.ApiMetric;
import com.healthtracker.repository.ApiMetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for batch writing metrics to database.
 */
@Service
public class MetricsBatchWriter {

    private static final Logger log = LoggerFactory.getLogger(MetricsBatchWriter.class);

    private final ApiMetricRepository apiMetricRepository;

    public MetricsBatchWriter(ApiMetricRepository apiMetricRepository) {
        this.apiMetricRepository = apiMetricRepository;
    }

    /**
     * Save a batch of metrics to database.
     *
     * @param metrics List of metrics to save
     * @return Number of metrics saved
     */
    @Transactional
    public int saveBatch(List<ApiMetric> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return 0;
        }

        try {
            long startTime = System.currentTimeMillis();

            // Batch save
            List<ApiMetric> saved = apiMetricRepository.saveAll(metrics);

            long duration = System.currentTimeMillis() - startTime;

            log.info("Saved batch: {} metrics in {}ms (avg: {}ms per metric)",
                    saved.size(), duration, duration / saved.size());

            return saved.size();

        } catch (Exception e) {
            log.error("Error saving batch of {} metrics", metrics.size(), e);
            throw e;
        }
    }
}
