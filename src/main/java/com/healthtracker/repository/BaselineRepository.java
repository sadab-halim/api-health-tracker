package com.healthtracker.repository;

import com.healthtracker.domain.Baseline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Baseline entities.
 */
@Repository
public interface BaselineRepository extends JpaRepository<Baseline, Long> {
}
