package com.healthtracker.repository;

import com.healthtracker.domain.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Anomaly entities.
 */
@Repository
public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {
}
