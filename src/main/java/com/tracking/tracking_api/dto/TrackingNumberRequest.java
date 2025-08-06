package com.tracking.tracking_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class TrackingNumberRequest {
    
    @NotBlank(message = "Origin country ID is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Origin country ID must be a valid ISO 3166-1 alpha-2 country code")
    private String originCountryId;
    
    @NotBlank(message = "Destination country ID is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Destination country ID must be a valid ISO 3166-1 alpha-2 country code")
    private String destinationCountryId;
    
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be at least 0.001 kg")
    @DecimalMax(value = "999999.999", message = "Weight cannot exceed 999999.999 kg")
    @Digits(integer = 6, fraction = 3, message = "Weight must have up to 6 digits before decimal and exactly 3 digits after")
    private BigDecimal weight;
    
    @NotNull(message = "Order creation timestamp is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;
    
    @NotNull(message = "Customer ID is required")
    private UUID customerId;
    
    @NotBlank(message = "Customer name is required")
    @Size(min = 1, max = 255, message = "Customer name must be between 1 and 255 characters")
    private String customerName;
    
    @NotBlank(message = "Customer slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Customer slug must be in kebab-case format")
    @Size(min = 1, max = 255, message = "Customer slug must be between 1 and 255 characters")
    private String customerSlug;
} 