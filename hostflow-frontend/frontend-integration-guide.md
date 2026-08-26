# RvanaFlow Backend Integration Guide

## 1. Project Overview

**Root:** `hostflow-backend/`

The backend is a Maven multi-module modular monolith with two main runtime apps:

- `app/`
  - Main backend service
  - Wires all business modules together
  - Spring Boot MVC, JPA, Flyway, RabbitMQ, Redis, JWT security
- `gateway-service/`
  - Spring Cloud Gateway reactive API gateway
  - Routes `/api/**` to the backend app
  - Performs JWT auth and tenant-based rate limiting

Modules included:

- `modules/core-common`
- `modules/core-config`
- `modules/core-persistence`
- `modules/core-tenancy`
- `modules/core-security`
- `modules/module-identity`
- `modules/module-property`
- `modules/module-booking`
- `modules/module-notification`
- `modules/module-crm`
- `modules/module-marketing`
- `modules/module-billing`
- `modules/module-analytics`

The root `pom.xml` uses Java 21 and Spring Boot 3.3.4.

---

## 2. Runtime & Startup

### app

- Main class: `com.hostflow.app.HostFlowApplication`
- Default port: `8080`
- Environment profile: `dev` by default
- Config file: `app/src/main/resources/application.yml` and `application-dev.yml`

### gateway-service

- Main class: `com.hostflow.gateway.GatewayApplication`
- Default port: `8085`
- Routes requests through Spring Cloud Gateway
- Authenticates and rate-limits traffic before forwarding to the app service

### Local dev environment values

- PostgreSQL: `localhost:5432`, database `hostflow`

- Redis: `localhost:6379`
- RabbitMQ: `localhost:5672`
- Keycloak issuer: `http://localhost:8080/realms/hostflow`
- Gateway Keycloak issuer: `http://localhost:8081/realms/hostflow`
- Allowed CORS origins:
  - `http://localhost:3000`
  - `http://localhost:9090`

---

## 3. Gateway Behavior

### Routing

- Gateway forwards `/api/**` to the app service
- Default app service URI: `http://localhost:8080`

### Rate limiting

- Tenant-aware rate limiting using `X-Tenant-ID`
- `RedisRateLimiter` configured with `50` requests/sec sustained and `100` burst capacity

### Auth

- Gateway validates JWT tokens on every request except:
  - `/actuator/health/**`
  - `/actuator/info`
- Uses OAuth2 resource server JWT validation

---

## 4. Common API Response Shape

All endpoints return `ApiResponse<T>`:

- `success` (boolean)
- `data` (object or list)
- `error` (object when failure)
- `timestamp`

Example success envelope:

```json
{
  "success": true,
  "data": { ... }
}
```

Example error envelope:

```json
{
  "success": false,
  "error": { ... }
}
```

Frontend should unwrap `data` and handle `success: false` uniformly.

---

## 5. Security & Authentication

### JWT auth

- Backend is a stateless OAuth2 resource server
- JWT is validated using `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- Important JWT claims:
  - `tenant_id`
  - `product_scope`

### Tenant handling

- `gateway-service` uses `X-Tenant-ID` for rate limiting
- Backend resolves tenant from JWT via `JwtTenantResolvingFilter`
- In dev/test, `X-Tenant-ID` may be accepted by a temporary header filter
- Production should rely on JWT claim, not raw header

### Authorization patterns

- Some endpoints require specific authority or role
- Common values:
  - `PRODUCT_XANUOS`
  - `PRODUCT_NAZILCO`
  - `ROLE_PLATFORM_ADMIN`

---

## 6. API Endpoints

### Marketing

`modules/module-marketing`

**Base path:** `/api/v1/marketing/campaigns`

Endpoints:

- `POST /api/v1/marketing/campaigns`
  - Request: `CreateCampaignRequest`
    - `propertyId` (UUID)
    - `name` (String)
    - `platform` (ContentPlatform enum)
    - `prompt` (String)
  - Response: `CampaignResponse`
    - `id`, `propertyId`, `name`, `platform`, `status`, `failureReason`
  - Requires `PRODUCT_XANUOS`

- `POST /api/v1/marketing/campaigns/{id}/generate`
  - Response: `CampaignResponse`
  - Requires `PRODUCT_XANUOS`

- `GET /api/v1/marketing/campaigns/{id}`
  - Response: `CampaignResponse`
  - Requires `PRODUCT_XANUOS`

- `PATCH /api/v1/marketing/campaigns/{id}/publish`
  - Response: `CampaignResponse`
  - Requires `PRODUCT_XANUOS`

---

### CRM

`modules/module-crm`

**Base path:** `/api/v1/crm/contacts`

Endpoints:

- `POST /api/v1/crm/contacts`
  - Request: `CreateContactRequest`
    - `fullName`, `email`, `phone`, `source`
  - Response: `ContactResponse`
  - Requires `PRODUCT_XANUOS`

- `GET /api/v1/crm/contacts/{id}`
  - Response: `ContactResponse`
  - Requires `PRODUCT_XANUOS`

- `PATCH /api/v1/crm/contacts/{id}/qualify`
  - Response: `ContactResponse`
  - Requires `PRODUCT_XANUOS`

- `POST /api/v1/crm/contacts/{id}/interactions`
  - Request: `LogInteractionRequest`
    - `type` (InteractionType enum)
    - `notes`
  - Response: success envelope with `null` data
  - Requires `PRODUCT_XANUOS`

---

### Booking

`modules/module-booking`

**Base path:** `/api/v1/bookings`

Endpoints:

- `POST /api/v1/bookings`
  - Request: `CreateBookingRequest`
    - `propertyId`, `checkIn`, `checkOut`, `totalPrice`
  - Response: `BookingResponse`
  - Requires `PRODUCT_NAZILCO`

- `GET /api/v1/bookings/{id}`
  - Response: `BookingResponse`
  - Requires authenticated user

- `PATCH /api/v1/bookings/{id}/cancel`
  - Response: `BookingResponse`
  - Requires authenticated user

---

### Property

`modules/module-property`

**Base path:** `/api/v1/properties`

Endpoints:

- `POST /api/v1/properties`
  - Request: `CreatePropertyRequest`
    - `name`, `propertyType`, `addressLine`, `city`, `country`
  - Response: `PropertyResponse`
  - Requires `PRODUCT_XANUOS`

- `GET /api/v1/properties/{id}`
  - Response: `PropertyResponse`
  - Requires authenticated user

- `PATCH /api/v1/properties/{id}/publish`
  - Response: `PropertyResponse`
  - Requires `PRODUCT_XANUOS`

---

### Billing

`modules/module-billing`

**Base path:** `/api/v1/billing/invoices`

Endpoints:

- `POST /api/v1/billing/invoices`
  - Request: `CreateInvoiceRequest`
    - `bookingId`, `billedUserId`, `amount`, `dueDate`
  - Response: `InvoiceResponse`
  - Requires `PRODUCT_XANUOS`

- `POST /api/v1/billing/invoices/batch`
  - Request: `BatchCreateInvoicesRequest`
    - `invoices`: list of `CreateInvoiceRequest`
  - Response: `BatchCreateInvoicesResponse`
    - `totalRequested`, `succeeded`, `failed`, `results[]`
  - Each `BatchInvoiceResult` contains:
    - `index`, `success`, `invoiceId`, `errorMessage`
  - Requires `PRODUCT_XANUOS`

- `GET /api/v1/billing/invoices/{id}`
  - Response: `InvoiceResponse`
  - Requires `PRODUCT_XANUOS`

- `PATCH /api/v1/billing/invoices/{id}/issue`
  - Response: `InvoiceResponse`
  - Requires `PRODUCT_XANUOS`

- `PATCH /api/v1/billing/invoices/{id}/pay`
  - Response: `InvoiceResponse`
  - Requires `PRODUCT_XANUOS`

---

### Analytics

`modules/module-analytics`

**Base path:** `/api/v1/analytics`

Endpoints:

- `GET /api/v1/analytics/property-occupancy`
  - Response: list of `PropertyOccupancyResponse`
    - `propertyId`, `propertyName`, `totalBookings`, `totalNightsBooked`, `totalRevenue`
  - Requires `PRODUCT_XANUOS`

- `GET /api/v1/analytics/monthly-revenue`
  - Response: list of `MonthlyRevenueResponse`
    - `month`, `invoicedTotal`, `paidTotal`, `invoiceCount`
  - Requires `PRODUCT_XANUOS`

---

### Identity / Organizations

`modules/module-identity`

**Base path:** `/api/v1/organizations`

Endpoints:

- `POST /api/v1/organizations`
  - Onboard organization
  - Response: `OrganizationResponse`
    - `id`, `name`, `slug`, `primaryProduct`, `active`
  - Requires `ROLE_PLATFORM_ADMIN`

---

## 7. Frontend Implementation Notes

### Base API URL

- Use gateway URL in dev: `http://localhost:8085/api/v1`
- Example endpoint: `http://localhost:8085/api/v1/properties/{id}`

### Auth headers

- `Authorization: Bearer <JWT>`
- In dev/test, use `X-Tenant-ID: <tenant-uuid>` if JWT tenant claim is absent

### CORS requirements

- `Content-Type` allowed
- `Authorization` allowed
- `X-Tenant-ID` allowed
- `Accept` allowed
- `Origin` allowed
- Credentials allowed

### Response handling

- Always unwrap the `ApiResponse` envelope
- On success use `response.data`
- On failure inspect `response.error`

### Suggested client structure

- Shared API client with:
  - base URL
  - token injection
  - error normalization
  - `ApiResponse` envelope handling
- Domain types for frontend state:
  - `Campaign`
  - `Contact`
  - `Booking`
  - `Property`
  - `Invoice`
  - `AnalyticsSummary`
  - `Organization`

### Security guidance

- Use role/authority metadata to determine UI access
- Protect admin operations behind `ROLE_PLATFORM_ADMIN`
- Protect product-specific flows behind `PRODUCT_XANUOS` or `PRODUCT_NAZILCO`

---

## 8. Quick Command Reference

### Run backend app

```bash
cd hostflow-backend
mvn -pl app clean spring-boot:run
```

### Run gateway

```bash
cd hostflow-backend/gateway-service
mvn clean spring-boot:run
```

### Recommended frontend call pattern

```js
const response = await fetch("http://localhost:8085/api/v1/properties/123", {
  headers: {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
    "X-Tenant-ID": tenantId,
  },
});
const body = await response.json();
if (!body.success) {
  throw new Error(body.error?.message || "API error");
}
return body.data;
```

---

## 9. Summary

This backend exposes a modular REST API behind a gateway. The frontend should:

- call the gateway at `http://localhost:8085/api/v1`
- send a valid JWT in `Authorization`
- optionally provide `X-Tenant-ID` in dev/test
- unwrap the `ApiResponse` envelope
- respect role/authority requirements for protected flows

Use the API routes above to align frontend screens with backend resources.
