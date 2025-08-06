package com.tracking.tracking_api.controller;

import com.tracking.tracking_api.dto.TrackingNumberRequest;
import com.tracking.tracking_api.dto.TrackingNumberResponse;
import com.tracking.tracking_api.service.TrackingNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TrackingController {

    private final TrackingNumberService trackingNumberService;

    /**
     * GET endpoint to generate the next tracking number
     * Accepts query parameters as specified in the requirements
     */
    @GetMapping("/next-tracking-number")
    public ResponseEntity<TrackingNumberResponse> getNextTrackingNumber(
            @RequestParam("origin_country_id") String originCountryId,
            @RequestParam("destination_country_id") String destinationCountryId,
            @RequestParam("weight") BigDecimal weight,
            @RequestParam("created_at") OffsetDateTime createdAt,
            @RequestParam("customer_id") UUID customerId,
            @RequestParam("customer_name") String customerName,
            @RequestParam("customer_slug") String customerSlug) {
        
        log.info("Received tracking number request for customer: {}", customerName);
        
        // Build request DTO from query parameters
        TrackingNumberRequest request = new TrackingNumberRequest();
        request.setOriginCountryId(originCountryId);
        request.setDestinationCountryId(destinationCountryId);
        request.setWeight(weight);
        request.setCreatedAt(createdAt);
        request.setCustomerId(customerId);
        request.setCustomerName(customerName);
        request.setCustomerSlug(customerSlug);

        try {
            TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);
            log.info("Successfully generated tracking number: {}", response.getTrackingNumber());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating tracking number for customer: {}", customerName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TrackingNumberResponse.builder()
                            .trackingNumber("ERROR")
                            .createdAt(OffsetDateTime.now())
                            .build());
        }
    }
    
    /**
     * Async version of the endpoint for better scalability
     */
    @GetMapping("/next-tracking-number/async")
    public CompletableFuture<ResponseEntity<TrackingNumberResponse>> getNextTrackingNumberAsync(
            @RequestParam("origin_country_id") String originCountryId,
            @RequestParam("destination_country_id") String destinationCountryId,
            @RequestParam("weight") BigDecimal weight,
            @RequestParam("created_at") OffsetDateTime createdAt,
            @RequestParam("customer_id") UUID customerId,
            @RequestParam("customer_name") String customerName,
            @RequestParam("customer_slug") String customerSlug) {
        
        log.info("Received async tracking number request for customer: {}", customerName);
        
        // Build request DTO from query parameters
        TrackingNumberRequest request = new TrackingNumberRequest();
        request.setOriginCountryId(originCountryId);
        request.setDestinationCountryId(destinationCountryId);
        request.setWeight(weight);
        request.setCreatedAt(createdAt);
        request.setCustomerId(customerId);
        request.setCustomerName(customerName);
        request.setCustomerSlug(customerSlug);
        
        return trackingNumberService.generateTrackingNumberAsync(request)
                .thenApply(response -> {
                    log.info("Successfully generated async tracking number: {}", response.getTrackingNumber());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(throwable -> {
                    log.error("Error generating async tracking number for customer: {}", customerName, throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(TrackingNumberResponse.builder()
                                    .trackingNumber("ERROR")
                                    .createdAt(OffsetDateTime.now())
                                    .build());
                });
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tracking API is healthy");
    }
} 