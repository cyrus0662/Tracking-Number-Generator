package com.tracking.tracking_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracking_numbers", indexes = {
    @Index(name = "idx_tracking_number", columnList = "trackingNumber", unique = true),
    @Index(name = "idx_customer_id", columnList = "customerId"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingNumber {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tracking_number", nullable = false, unique = true, length = 16)
    private String trackingNumber;
    
    @Column(name = "origin_country_id", nullable = false, length = 2)
    private String originCountryId;
    
    @Column(name = "destination_country_id", nullable = false, length = 2)
    private String destinationCountryId;
    
    @Column(name = "weight", nullable = false, precision = 10, scale = 3)
    private BigDecimal weight;
    
    @Column(name = "order_created_at", nullable = false)
    private OffsetDateTime orderCreatedAt;
    
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "customer_slug", nullable = false)
    private String customerSlug;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Version
    @Column(name = "version")
    private Long version;
} 