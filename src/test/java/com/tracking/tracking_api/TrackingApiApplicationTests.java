package com.tracking.tracking_api;

import com.tracking.tracking_api.dto.TrackingNumberRequest;
import com.tracking.tracking_api.dto.TrackingNumberResponse;
import com.tracking.tracking_api.service.TrackingNumberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TrackingApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TrackingApiApplicationTests {

    @Autowired
    private TrackingNumberService trackingNumberService;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
    }

    @Test
    void testTrackingNumberGeneration() {
        // Given
        TrackingNumberRequest request = createValidRequest();

        // When
        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getTrackingNumber());
        assertTrue(Pattern.matches("^[A-Z0-9]{1,16}$", response.getTrackingNumber()));
        assertNotNull(response.getCreatedAt());
        assertEquals(request.getOriginCountryId(), response.getOriginCountryId());
        assertEquals(request.getDestinationCountryId(), response.getDestinationCountryId());
        assertEquals(request.getCustomerName(), response.getCustomerName());
        assertEquals(request.getCustomerSlug(), response.getCustomerSlug());
    }

    @Test
    void testTrackingNumberUniqueness() {
        // Given
        TrackingNumberRequest request1 = createValidRequest();
        TrackingNumberRequest request2 = createValidRequest();

        // When
        TrackingNumberResponse response1 = trackingNumberService.generateTrackingNumber(request1);
        TrackingNumberResponse response2 = trackingNumberService.generateTrackingNumber(request2);

        // Then
        assertNotEquals(response1.getTrackingNumber(), response2.getTrackingNumber());
    }

    @Test
    void testApiEndpoint() {
        // Given
        String url = String.format("http://localhost:%d/api/v1/next-tracking-number", port);
        String params = "?origin_country_id=MY" +
                "&destination_country_id=ID" +
                "&weight=1.234" +
                "&created_at=2018-11-20T19:29:32Z" +
                "&customer_id=de619854-b59b-425e-9db4-943979e1bd49" +
                "&customer_name=RedBox%20Logistics" +
                "&customer_slug=redbox-logistics";

        // When
        ResponseEntity<TrackingNumberResponse> response = restTemplate.getForEntity(
                url + params, TrackingNumberResponse.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTrackingNumber());
        assertTrue(Pattern.matches("^[A-Z0-9]{1,16}$", response.getBody().getTrackingNumber()));
    }

    @Test
    void testAsyncApiEndpoint() {
        // Given
        String url = String.format("http://localhost:%d/api/v1/next-tracking-number/async", port);
        String params = "?origin_country_id=MY" +
                "&destination_country_id=ID" +
                "&weight=1.234" +
                "&created_at=2018-11-20T19:29:32Z" +
                "&customer_id=de619854-b59b-425e-9db4-943979e1bd49" +
                "&customer_name=RedBox%20Logistics" +
                "&customer_slug=redbox-logistics";

        // When
        ResponseEntity<TrackingNumberResponse> response = restTemplate.getForEntity(
                url + params, TrackingNumberResponse.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTrackingNumber());
        assertTrue(Pattern.matches("^[A-Z0-9]{1,16}$", response.getBody().getTrackingNumber()));
    }

    @Test
    void testHealthEndpoint() {
        // Given
        String url = String.format("http://localhost:%d/api/v1/health", port);

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tracking API is healthy", response.getBody());
    }

    @Test
    void testInvalidCountryCode() {
        // Given
        String url = String.format("http://localhost:%d/api/v1/next-tracking-number", port);
        String params = "?origin_country_id=INVALID" +
                "&destination_country_id=ID" +
                "&weight=1.234" +
                "&created_at=2018-11-20T19:29:32+08:00" +
                "&customer_id=de619854-b59b-425e-9db4-943979e1bd49" +
                "&customer_name=RedBox%20Logistics" +
                "&customer_slug=redbox-logistics";

        // When
        ResponseEntity<Object> response = restTemplate.getForEntity(url + params, Object.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testInvalidWeight() {
        // Given
        String url = String.format("http://localhost:%d/api/v1/next-tracking-number", port);
        String params = "?origin_country_id=MY" +
                "&destination_country_id=ID" +
                "&weight=-1.234" +
                "&created_at=2018-11-20T19:29:32+08:00" +
                "&customer_id=de619854-b59b-425e-9db4-943979e1bd49" +
                "&customer_name=RedBox%20Logistics" +
                "&customer_slug=redbox-logistics";

        // When
        ResponseEntity<Object> response = restTemplate.getForEntity(url + params, Object.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private TrackingNumberRequest createValidRequest() {
        TrackingNumberRequest request = new TrackingNumberRequest();
        request.setOriginCountryId("MY");
        request.setDestinationCountryId("ID");
        request.setWeight(new BigDecimal("1.234"));
        request.setCreatedAt(OffsetDateTime.parse("2018-11-20T19:29:32+08:00"));
        request.setCustomerId(UUID.fromString("de619854-b59b-425e-9db4-943979e1bd49"));
        request.setCustomerName("RedBox Logistics");
        request.setCustomerSlug("redbox-logistics");
        return request;
    }
}
