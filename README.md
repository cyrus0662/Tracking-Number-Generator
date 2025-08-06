# Scalable Tracking API

A high-performance, scalable REST API for generating unique tracking numbers with support for concurrent requests and horizontal scaling.

## Features

- **Unique Tracking Numbers**: Generates tracking numbers matching the regex pattern `^[A-Z0-9]{1,16}$`
- **Concurrent Processing**: Supports multiple concurrent requests without degradation
- **Horizontal Scalability**: Designed to scale across multiple instances
- **Creative Algorithm**: Uses request parameters to create meaningful tracking numbers
- **Async Support**: Both synchronous and asynchronous endpoints available
- **Comprehensive Validation**: Input validation with detailed error messages
- **Database Persistence**: Stores tracking numbers with full audit trail

## API Endpoints

### 1. Generate Tracking Number (Synchronous)
```
GET /api/v1/next-tracking-number
```

### 2. Generate Tracking Number (Asynchronous)
```
GET /api/v1/next-tracking-number/async
```

### 3. Health Check
```
GET /api/v1/health
```

## Query Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `origin_country_id` | String | Origin country code (ISO 3166-1 alpha-2) | "MY" |
| `destination_country_id` | String | Destination country code (ISO 3166-1 alpha-2) | "ID" |
| `weight` | BigDecimal | Order weight in kilograms (up to 3 decimal places) | "1.234" |
| `created_at` | OffsetDateTime | Order creation timestamp (RFC 3339) | "2018-11-20T19:29:32+08:00" |
| `customer_id` | UUID | Customer's unique identifier | "de619854-b59b-425e-9db4-943979e1bd49" |
| `customer_name` | String | Customer's name | "RedBox Logistics" |
| `customer_slug` | String | Customer's name in kebab-case | "redbox-logistics" |

## Example Request

```bash
curl -X GET "http://localhost:8080/api/v1/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32+08:00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics"
```

## Example Response

```json
{
  "trackingNumber": "MY234RE18A5",
  "createdAt": "2024-01-15T10:30:45+00:00",
  "originCountryId": "MY",
  "destinationCountryId": "ID",
  "customerName": "RedBox Logistics",
  "customerSlug": "redbox-logistics"
}
```

## Tracking Number Algorithm

The tracking number generation algorithm incorporates multiple request parameters:

1. **Prefix**: Origin country code (2 characters)
2. **Weight Code**: Last 3 digits of weight × 1000
3. **Customer Code**: First 2 letters of customer slug (uppercase)
4. **Timestamp Code**: Last 2 digits of year + month
5. **Random Component**: 2 random alphanumeric characters
6. **Sequence Number**: Last 2 digits of atomic counter

This ensures uniqueness while creating meaningful, traceable tracking numbers.

## Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Database**: H2 (in-memory for development)
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Bean Validation (Jakarta)
- **Async Processing**: Spring Async with ThreadPoolTaskExecutor
- **Build Tool**: Maven
- **Testing**: JUnit 5 with Spring Boot Test

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd tracking-api
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the API**
   - API Base URL: `http://localhost:8080/api/v1`
   - H2 Console: `http://localhost:8080/h2-console` (for development)

### Running Tests

```bash
./mvnw test
```

## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:trackingdb
spring.datasource.maximum-pool-size=20

# Async Configuration
spring.task.execution.pool.core-size=10
spring.task.execution.pool.max-size=50
spring.task.execution.pool.queue-capacity=100

# Server Configuration
server.port=8080
```

## Error Handling

The API provides comprehensive error handling:

- **Validation Errors**: Detailed field-level validation messages
- **Type Mismatch**: Clear error messages for parameter type issues
- **Generic Errors**: Graceful handling of unexpected errors
- **HTTP Status Codes**: Appropriate status codes for different error types

## Testing

The application includes comprehensive tests:

- **Unit Tests**: Service layer testing
- **Integration Tests**: API endpoint testing
- **Validation Tests**: Input validation testing
- **Concurrency Tests**: Concurrent request handling

## API Documentation

### Response Format

All successful responses return JSON with the following structure:

```json
{
  "trackingNumber": "string",
  "createdAt": "datetime",
  "originCountryId": "string",
  "destinationCountryId": "string",
  "customerName": "string",
  "customerSlug": "string"
}
```

### Error Response Format

Error responses include:

```json
{
  "timestamp": "datetime",
  "status": "number",
  "error": "string",
  "message": "string",
  "errors": "object (optional)"
}
```