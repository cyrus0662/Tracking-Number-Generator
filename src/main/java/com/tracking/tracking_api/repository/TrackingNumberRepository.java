package com.tracking.tracking_api.repository;

import com.tracking.tracking_api.entity.TrackingNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingNumberRepository extends JpaRepository<TrackingNumber, Long> {
    
    /**
     * Check if a tracking number already exists
     */
    boolean existsByTrackingNumber(String trackingNumber);
    
    /**
     * Find tracking number by its value
     */
    Optional<TrackingNumber> findByTrackingNumber(String trackingNumber);
    
    /**
     * Get count of tracking numbers for a specific customer
     */
    long countByCustomerId(UUID customerId);
    
    /**
     * Get the latest tracking number for a customer to help with sequential generation
     */
    @Query("SELECT t FROM TrackingNumber t WHERE t.customerId = :customerId ORDER BY t.createdAt DESC LIMIT 1")
    Optional<TrackingNumber> findLatestByCustomerId(@Param("customerId") UUID customerId);
} 