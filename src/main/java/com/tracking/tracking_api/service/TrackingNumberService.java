package com.tracking.tracking_api.service;

import com.tracking.tracking_api.dto.TrackingNumberRequest;
import com.tracking.tracking_api.dto.TrackingNumberResponse;
import com.tracking.tracking_api.entity.TrackingNumber;
import com.tracking.tracking_api.repository.TrackingNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingNumberService {
    
    private final TrackingNumberRepository trackingNumberRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final AtomicLong sequenceCounter = new AtomicLong(0);
    
    private static final String ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int MAX_RETRY_ATTEMPTS = 10;
    
    /**
     * Generate a unique tracking number asynchronously
     */
    @Async
    @Transactional
    public CompletableFuture<TrackingNumberResponse> generateTrackingNumberAsync(TrackingNumberRequest request) {
        return CompletableFuture.completedFuture(generateTrackingNumber(request));
    }
    
    /**
     * Generate a unique tracking number synchronously
     */
    @Transactional
    public TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest request) {
        log.info("Generating tracking number for customer: {}", request.getCustomerName());
        
        String trackingNumber = generateUniqueTrackingNumber(request);
        
        TrackingNumber entity = TrackingNumber.builder()
                .trackingNumber(trackingNumber)
                .originCountryId(request.getOriginCountryId())
                .destinationCountryId(request.getDestinationCountryId())
                .weight(request.getWeight())
                .orderCreatedAt(request.getCreatedAt())
                .customerId(request.getCustomerId())
                .customerName(request.getCustomerName())
                .customerSlug(request.getCustomerSlug())
                .createdAt(OffsetDateTime.now())
                .build();
        
        TrackingNumber savedEntity = trackingNumberRepository.save(entity);
        
        log.info("Generated tracking number: {} for customer: {}", trackingNumber, request.getCustomerName());
        
        return TrackingNumberResponse.builder()
                .trackingNumber(savedEntity.getTrackingNumber())
                .createdAt(savedEntity.getCreatedAt())
                .originCountryId(savedEntity.getOriginCountryId())
                .destinationCountryId(savedEntity.getDestinationCountryId())
                .customerName(savedEntity.getCustomerName())
                .customerSlug(savedEntity.getCustomerSlug())
                .build();
    }
    
    /**
     * Generate a unique tracking number with creative algorithm
     */
    private String generateUniqueTrackingNumber(TrackingNumberRequest request) {
        String trackingNumber;
        int attempts = 0;
        
        do {
            trackingNumber = createTrackingNumber(request);
            attempts++;
            
            if (attempts > MAX_RETRY_ATTEMPTS) {
                throw new RuntimeException("Unable to generate unique tracking number after " + MAX_RETRY_ATTEMPTS + " attempts");
            }
        } while (trackingNumberRepository.existsByTrackingNumber(trackingNumber));
        
        return trackingNumber;
    }
    
    /**
     * Create a tracking number using creative algorithm incorporating request parameters
     */
    private String createTrackingNumber(TrackingNumberRequest request) {
        StringBuilder sb = new StringBuilder();
        
        // Prefix: First 2 letters from origin country
        sb.append(request.getOriginCountryId().substring(0, 2));
        
        // Middle: Weight-based encoding (last 3 digits of weight * 1000)
        BigDecimal weightValue = request.getWeight().multiply(BigDecimal.valueOf(1000));
        String weightCode = String.format("%03d", weightValue.remainder(BigDecimal.valueOf(1000)).intValue());
        sb.append(weightCode);
        
        // Customer identifier: First 2 letters of customer slug (uppercase)
        String customerCode = request.getCustomerSlug().substring(0, Math.min(2, request.getCustomerSlug().length())).toUpperCase();
        sb.append(customerCode);
        
        // Timestamp component: Last 2 digits of year + month
        String timestampCode = request.getCreatedAt().getYear() % 100 + 
                              String.format("%02d", request.getCreatedAt().getMonthValue());
        sb.append(timestampCode);
        
        // Random component: 2 random alphanumeric characters
        for (int i = 0; i < 2; i++) {
            sb.append(ALPHANUMERIC_CHARS.charAt(secureRandom.nextInt(ALPHANUMERIC_CHARS.length())));
        }
        
        // Sequence number: Last 2 digits of atomic counter
        String sequenceCode = String.format("%02d", sequenceCounter.incrementAndGet() % 100);
        sb.append(sequenceCode);
        
        String result = sb.toString();
        
        // Ensure the result matches the regex pattern and is not longer than 16 characters
        if (result.length() > 16) {
            result = result.substring(0, 16);
        }
        
        // Pad with random characters if shorter than 12
        while (result.length() < 12) {
            result += ALPHANUMERIC_CHARS.charAt(secureRandom.nextInt(ALPHANUMERIC_CHARS.length()));
        }
        
        return result;
    }
} 