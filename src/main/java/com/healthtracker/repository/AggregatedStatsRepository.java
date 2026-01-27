package com.healthtracker.repository;

import com.healthtracker.domain.AggregatedStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for AggregatedStats entities.
 */
@Repository
public interface AggregatedStatsRepository extends JpaRepository<AggregatedStats, Long> {
}
