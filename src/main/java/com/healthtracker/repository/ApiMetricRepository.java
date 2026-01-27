package com.healthtracker.repository;

import com.healthtracker.domain.ApiMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for ApiMetric entities.
 */
@Repository
public interface ApiMetricRepository extends JpaRepository<ApiMetric, Long> {

    /**
     * Find metrics by API name within time range.
     */
    @Query("SELECT m FROM ApiMetric m WHERE m.apiName = :apiName " +
            "AND m.timestamp BETWEEN :start AND :end ORDER BY m.timestamp DESC")
    List<ApiMetric> findByApiNameAndTimeRange(
            @Param("apiName") String apiName,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    /**
     * Count metrics by API name.
     */
    long countByApiName(String apiName);

    /**
     * Find latest metrics for an API.
     */
    List<ApiMetric> findTop100ByApiNameOrderByTimestampDesc(String apiName);
}
