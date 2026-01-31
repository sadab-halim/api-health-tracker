package com.healthtracker.domain.repository;

import com.healthtracker.domain.model.ApiMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ApiMetricRepository extends JpaRepository<ApiMetric, Long> {

    /**
     * Find metrics by API name within a time range
     */
    List<ApiMetric> findByApiNameAndTimestampBetween(
            String apiName,
            Instant startTime,
            Instant endTime
    );

    /**
     * Count metrics by API name
     */
    long countByApiName(String apiName);

    /**
     * Find recent metrics for an API
     */
    @Query("SELECT m FROM ApiMetric m WHERE m.apiName = :apiName ORDER BY m.timestamp DESC")
    List<ApiMetric> findRecentByApiName(@Param("apiName") String apiName);
}
