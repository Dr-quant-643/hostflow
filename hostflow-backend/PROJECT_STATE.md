# HostFlow Backend — Project State (as of this handoff)

## Project Identity

HostFlow = parent brand. Two products: XanuOS (property/business ops, B2B) and
NazilCo (guest-facing booking/discovery marketplace, B2C, cross-tenant).
Backend location: C:\Users\YourUsername\Desktop\hostflow-backend
Frontend location (not yet built beyond scaffold): C:\Users\YourUsername\Desktop\hostflow-frontend
Tools: Maven 3.9.16, Java 21, Git Bash, VS Code, Docker Desktop required.

## Locked Architecture Decisions

- Spring Boot modular monolith (Java 21), Maven multi-module.
- PostgreSQL + Row-Level Security for multi-tenancy (tenant_id column + RLS policy
  per tenant-owned table). Fail-closed: no tenant context = zero rows.
- Redis: cache, gateway rate limiting.
- RabbitMQ: shared hostflow.direct/hostflow.topic/hostflow.fanout exchanges, ONE
  shared hostflow.dlq (not per-module DLQs), config-driven via
  hostflow.rabbitmq.\* in application-dev.yml, bound dynamically by
  core-messaging's HostFlowRabbitTopologyConfig.
- Keycloak: OAuth2/OIDC. Realm "hostflow". FIVE frontend clients registered
  (hostflow-web, nazilco-web, hostflow-admin, nazilco-admin, xanuos-console) +
  one service-account client (hostflow-admin-cli, needs manage-users role) for
  KeycloakProvisioningService's Admin API calls.
- Spring Cloud Gateway (gateway-service module): JWT validation, tenant-aware
  Redis rate limiting, aggregated /admin/health.
- Storage: S3-compatible (MinIO locally, real S3/equivalent in prod) via
  core-storage module (AWS SDK v2, presigned URLs, never raw public URLs).
- Guests (NazilCo) are TENANT-LESS by design: GuestProfile extends BaseEntity
  (not TenantScopedEntity). Product scope (XANUOS/NAZILCO) is a Keycloak claim +
  Spring authority (PRODUCT_XANUOS/PRODUCT_NAZILCO). A guest JWT has
  product_scope=[NAZILCO] and NO tenant_id claim.
- Cross-tenant reads (platform-admin screens, public NazilCo browsing, guest
  self-service) use a SECOND DataSource authenticated as hostflow_platform_admin
  (BYPASSRLS), exposed as the `platformAdminJdbcTemplate` bean in app module.
  Every query on this bean MUST filter explicitly in code — RLS does not protect
  it. This is the deliberate, documented pattern for all guest/cross-tenant reads.
- AI strategy: Claude API called DIRECTLY from Spring Boot (no FastAPI, no
  intermediate Python service). Dynamic pricing, recommendations, WhatsApp
  assistant groundwork, marketing/customer analytics, NazilCo AI search all route
  through Claude API. AI content generation and AI WhatsApp (as originally
  scoped) were REMOVED entirely per explicit decision — see "Removed" section.
- Payments: MPESA integration + Products/Plans/Subscriptions are DELIBERATELY
  DEFERRED to the very end of the project, by explicit instruction. Do not build
  until told.

## Full Module List (Maven modules, in dependency/build order)

### Foundational (6)

1. core-common — exceptions, ApiResponse, PageResponse. Zero framework deps.
2. core-config — GlobalExceptionHandler, DataIntegrityExceptionHandler (maps DB
   constraint violations, e.g. booking overlap, to clean 409s), CORS, Jackson.
3. core-persistence — BaseEntity, Flyway wiring, creates hostflow_app/
   hostflow_migrations/hostflow_platform_admin DB roles, current_tenant_id() SQL fn.
4. core-tenancy — TenantContext (ThreadLocal), TenantScopedEntity, custom
   TenantAwareJpaTransactionManager (SET LOCAL app.current_tenant per transaction).
5. core-security — Keycloak OAuth2 Resource Server, HostFlowJwtAuthenticationConverter
   (JWT claims -> Spring authorities), JwtTenantResolvingFilter, SecurityConfig
   (permitAll list documented below).
6. core-storage — S3Client/S3Presigner beans, StorageService (upload/presigned-URL/delete).
7. core-messaging — HostFlowRabbitProperties (bound to hostflow.rabbitmq.\* YAML),
   HostFlowRabbitTopologyConfig (declares exchanges/queues/bindings dynamically
   from that YAML), DomainEventMessage + DomainEventPublisher (shared event
   publishing helper), QueueNames/RoutingKeys constants classes.

### Gateway (standalone runnable, own port from hostflow.gateway config)

8. gateway-service — Spring Cloud Gateway, TenantKeyResolver, RouteConfig,
   GatewaySecurityConfig + GatewayJwtAuthenticationConverter (reactive JWT
   converter — REQUIRED, do not rely on Spring's default), AdminHealthService
   (aggregates app's /actuator/health into GET /admin/health, PLATFORM_ADMIN only).

### Business modules (independent of each other, all depend on foundational set)

9. module-identity — Organization (=tenant, extends BaseEntity NOT
   TenantScopedEntity), User, GuestProfile (tenant-less, extends BaseEntity),
   KeycloakProvisioningService (org admin users), GuestKeycloakProvisioningService
   (guests, sets product_scope=[NAZILCO] only, no tenant_id),
   GuestRegistrationService (POST /guests/register), OrgUserAdminService
   (platform-admin manages any org's users), SelfServiceUserAdminService (owner/
   manager manages THEIR OWN org's staff, blocks granting PLATFORM_ADMIN),
   TenantEventPublisher (fires on org onboard/rename).
10. module-property — Property, PropertyDocument (documentType: PHOTO/
    FLOOR_PLAN/CONTRACT/INSURANCE/OTHER — upload() takes type param, size/content-type
    validation varies by type), pgvector embeddings table (schema only, unpopulated —
    will be used by Claude-based recommendation engine), PropertyEventPublisher
    (created/updated/archived events), PropertyService.archive() method.
11. module-booking — Booking (half-open interval overlap logic), BookingEventPublisher
    (created/confirmed/cancelled/expired), DigitalCheckIn entity (ID doc upload tied
    to booking). DB-level EXCLUDE constraint (btree_gist) on (property_id,
    daterange(check_in,check_out)) WHERE status blocking — real race-condition fix,
    not just app-level.
12. module-notification — NotificationTemplate, NotificationLog, REAL delivery via
    EmailDeliveryService (JavaMailSender/SMTP), SmsDeliveryService (Africa's Talking
    HTTP API), PushDeliveryService (FCM legacy API), WhatsAppDeliveryService (WhatsApp
    Business Cloud API) — NO MORE log.info() SIMULATION, these are real provider calls.
    4 RabbitMQ listeners (email/sms/push/whatsapp), each on its own real queue.
    NotificationPublisher requires recipientAddress explicitly (caller must resolve
    email/phone/token before calling — module stays decoupled from identity/property).
13. module-crm — Contact (lead lifecycle), Interaction (append-only), SupportTicket
    (priority/status/assignment, writes an Interaction on every state change when a
    contact is linked), InteractionType.SUPPORT_REQUEST enum value exists for real.
14. module-marketing — MarketingCampaign SIMPLIFIED (DRAFT/PUBLISHED/ARCHIVED only —
    AI content generation entirely REMOVED, see "Removed" section). Now just a content
    record with manual `content` field, no AI/RabbitMQ/WebFlux dependencies.
15. module-billing — Invoice, Payment (PaymentService/PaymentController/
    PaymentEventPublisher newly built — this was a real gap, Payment entity existed
    with no service layer until this phase), Expense, Budget (upsert semantics,
    variance reporting), InvoiceRowWriter (separate bean — fixes REQUIRES_NEW
    self-invocation bug), Batch API (sync, <=100 rows, per-row isolation).
16. module-analytics — Materialized views (mv_property_occupancy_summary,
    mv_monthly_revenue_summary) — NOT RLS-protected (Postgres limitation on
    materialized views), tenant filtering enforced EXPLICITLY in
    AnalyticsService/repository layer (documented as the PRIMARY enforcement, not
    defense-in-depth). refresh_analytics_views() SQL fn, called by
    AnalyticsRefreshJob (app module, every 15 min).
17. module-maintenance — WorkOrder (lifecycle: OPEN/ASSIGNED/IN_PROGRESS/COMPLETED/
    CANCELLED), Asset (warranty tracking), MaintenanceSchedule (recurring, feeds
    PreventiveMaintenanceJob in app which auto-generates WorkOrders daily 3am).
18. module-rental — RentalTenant (linkedUserId nullable, NOT auto-linked to any
    User yet — see Open Gaps), Lease (activate() generates full RentPayment schedule
    up front, one row per month), RentPayment.
19. module-office — MeetingRoom, RoomBooking (Instant-based overlap check, same
    half-open-interval logic as Booking), Visitor (check-in/out).
20. module-mall — RetailUnit, RetailTenant (separate entity from RentalTenant —
    different business fields), MallEvent (publicly readable), ParkingSession
    (flat-rate-per-hour, rounds up to nearest hour).
21. module-review — Review (one per booking, unique constraint on booking_id,
    rating 1-5, owner can respond). Guest can only review a CHECKED_OUT booking
    they own (enforced in GuestReviewOrchestrator, app module).
22. module-platform-admin — FeatureFlag (global + per-org override, resolution:
    org override > global default > false), AuditLogEntry (append-only,
    AuditLogService.record() — NOW HAS REAL CALLERS via DomainAuditEventConsumer,
    see below). Neither table is RLS-protected (platform-wide by design, like
    Organization).

### app module (bootstrap, wires everything, standalone runnable)

Key contents beyond the original 15-module build:

- platformAdminJdbcTemplate bean (PlatformAdminDataSourceConfig) — the
  cross-tenant read mechanism used throughout.
- PublicPropertyController/PublicPropertyQueries/PublicAvailabilityQueries/
  PublicPropertyPhotoQueries — anonymous NazilCo browsing (GET /properties/public,
  .../search, .../{id}, .../{id}/photos — all permitAll).
- GuestBookingOrchestrator/GuestBookingWriter — resolves booking tenant FROM THE
  PROPERTY being booked (guest has no tenant of their own). Sets TenantContext
  explicitly before calling into the writer bean.
- GuestDashboardService, GuestInvoiceQueries, GuestNotificationQueries,
  RentalPortalQueries, PublicVenueDirectoryController, GuestReviewOrchestrator/
  GuestReviewWriter — all guest-facing cross-tenant reads, same pattern.
- PlatformAdminController, PlatformBillingQueries, PlatformBookingQueries,
  PlatformOrganizationQueries, PlatformMonitoringQueries — platform-admin
  cross-tenant screens (billing, bookings, orgs, monitoring snapshot).
- DomainAuditEventConsumer — listens to ALL domain event queues (booking/property/
  payment/tenant), writes every event to AuditLogService (this is what finally
  gave the audit log real callers). ALSO attempts a best-effort "booking
  confirmed" email (fails silently/logs a warning if no booking_confirmed
  template exists for that org yet — nothing auto-creates this template,
  flagged as an open gap).
- OverdueInvoiceSweepJob — FIXED to use platformAdminJdbcTemplate directly (was
  broken originally, silently processed zero rows due to TenantContext never
  being set for scheduled jobs).
- ExpireStalePendingBookingsJob — new, cancels PENDING bookings >24h old, fires
  the expired event (closes a previously-inert queue).
- PreventiveMaintenanceJob — new, generates WorkOrders from due
  MaintenanceSchedules daily.
- InvoiceRowWriter fix lives IN module-billing (not app) — correct dependency
  direction preserved.

## Explicit Deletions (removed entirely, confirm they're gone if you see any trace)

- AI content generation: ContentGenerationClient, FastApiProperties,
  ContentGenerationMessage/Publisher/Consumer, MarketingRabbitMQConfig,
  GeneratedContent entity+repository — ALL DELETED from module-marketing.
  CampaignStatus simplified to DRAFT/PUBLISHED/ARCHIVED only.
- AI WhatsApp "assistant" as an AI concept — dropped. (Real WhatsApp notification
  DELIVERY still exists and is correct — that's a different thing: sending a
  plain WhatsApp message via the Business API is NOT the same as an "AI
  WhatsApp assistant" that holds a conversation, which was dropped.)
- hostflow.rabbitmq.queues.ai._ and .analytics._ — removed from YAML and from
  HostFlowRabbitProperties/HostFlowRabbitTopologyConfig. No queue-based AI or
  analytics pipeline exists; Claude API will be called synchronously instead.

## Real Fixes Applied This Phase

- Booking race condition (Postgres EXCLUDE constraint + btree_gist, V23 migration)
  — genuinely fixed, not just documented.
- WHATSAPP notification channel — real dedicated queue + real delivery service
  (was previously falling back incorrectly to the email queue).
- DataIntegrityViolationException now maps to a clean 409, not a raw 500 leak.

## Consciously Deferred / NOT Built (flagged, not silently dropped)

- MPESA + Products/Plans/Subscriptions — deferred to the END, per explicit instruction.
- Virtual tours, Device management (push token registration), Corporate/coworking
  desk booking — flagged during Category 5 build-out, awaiting your decision on
  whether to build now or skip.
- MFA — this is a KEYCLOAK CONSOLE CONFIGURATION task, not code. Steps are in the
  chat history: Realm Settings > Authentication > OTP Policy + Browser flow >
  "Browser - Conditional OTP" execution.

## Known Open Gaps (real, not yet fixed)

1. ~~RentalTenant.linkToUser() exists but nothing calls it~~ — FIXED: added
   RentalTenantLinkOrchestrator (app module) + RentalTenantService.linkToUser(),
   new POST /api/v1/rental/portal/link lets a signed-in NazilCo guest claim an
   unlinked RentalTenant record by email match (cross-tenant lookup via
   platformAdminJdbcTemplate, same pattern as GuestBookingOrchestrator).
2. ~~No standard notification templates auto-created on org onboarding~~ —
   FIXED: added NotificationTemplateSeedService (module-notification) and wired
   it into DomainAuditEventConsumer.tenantCreated(). Also fixed two bugs that
   were silently breaking the "booking confirmed" auto-email even with a
   template present: (a) bookingConfirmed()/tenantCreated() were @Transactional
   at the RabbitListener level, which opened the transaction BEFORE
   TenantContext.set() ran, so TenantAwareJpaTransactionManager's SET LOCAL saw
   no tenant — removed the outer @Transactional; (b) resolveGuestEmail() queried
   guest_profiles.id/users.id instead of keycloak_id, which never matches the
   Keycloak-subject-based guestUserId used everywhere else in the app module —
   fixed to query by keycloak_id.
3. ~~Push notification device tokens~~ — FIXED: added DeviceToken entity/
   repository/service (module-notification, tenant-less like GuestProfile) and
   POST/unregister endpoints at /api/v1/notifications/devices. Wiring an actual
   push send (calling activeTokensFor() before NotificationService.send()) is
   still left to whichever future caller needs it — this only adds the missing
   storage/registration layer.
4. ~~Signed URLs (property photos/documents) expire in 1 hour with no refresh
   mechanism on the frontend side yet~~ — FIXED: usePropertyPhotos (public,
   nazilco-web) and usePropertyDocuments (owner-side, hostflow-web) both now
   set `refetchInterval: 45min` in @hostflow/api-client, so a page left open
   past the 1-hour presigned-URL expiry gets a fresh set of URLs before the
   old ones go stale, without needing a window refocus/remount to trigger it.
5. ~~Keycloak realm must be manually re-imported + client secrets distributed~~
   — DONE: realm auto-imports via docker-compose's `--import-realm` mount, all
   6 client secrets confirmed matching `dev-secret-<clientId>` across every
   app's `.env.local`, and a full real login (PKCE, Keycloak credential
   submit, callback, session) has been verified end-to-end for hostflow-web
   against the real owner account. Also found and fixed a real bug in this
   pass: `decodeUserInfo` (packages/auth) was reading a nonexistent
   `authorities` claim directly off the Keycloak JWT — Keycloak never puts
   one there, only the backend's HostFlowJwtAuthenticationConverter derives
   it (from `realm_access.roles`/`product_scope`) at request time. Every real
   login was silently getting zero authorities and bouncing to
   /access-denied until the frontend was fixed to do the same derivation.
6. Real provider credentials needed (not yet supplied, currently placeholder
   env vars): Africa's Talking (SMS), FCM (push), WhatsApp Business Cloud API
   (Meta developer account + phone number ID + token) — this is a credentials
   problem, not a code problem: SmsDeliveryService/PushDeliveryService/
   WhatsAppDeliveryService (module-notification) were all re-checked this
   pass and are already correct, real integrations (right endpoints, auth
   headers, payload shapes) that will work the moment real credentials are
   supplied. SMTP itself is separately solved for local dev — see item 7.
7. ~~Docker Compose file for one-command local startup — does NOT exist~~ —
   DONE: `hostflow-backend/docker-compose.yml` (project `hfdev`) brings up
   Postgres/pgvector, Redis, RabbitMQ, Keycloak, MinIO, and MailHog (a local
   SMTP catcher — Keycloak's realm now has real working SMTP pointed at it,
   closing the SMTP part of item 6, view sent mail at localhost:8025) in one
   `docker compose up -d`. app + gateway-service still start separately
   (`mvn spring-boot:run` per module) since it's a mixed Docker+bare-Maven
   dev setup, not a full docker-compose build of the app images.
8. CI/CD pipeline — never built.
9. Vault/secrets manager — decided architecturally, never implemented; all
   secrets are plain env vars currently.

## What's NEXT (the actual next phase to build)

Claude API integration — the last major backend phase before payments/plans:

1. New module: module-ai (Claude API client, using Anthropic's Java SDK or plain
   WebClient + REST calls to api.anthropic.com).
2. Dynamic pricing (property price suggestions via Claude, given occupancy/
   season/comparable data from module-analytics).
3. AI recommendation engine (property recommendations for NazilCo guests —
   likely Claude-based semantic matching using property descriptions +
   pgvector embeddings, OR simpler prompt-based recommendation without vectors
   — needs a decision).
4. AI marketing/customer analytics (Claude-generated insights from existing
   analytics data).
5. NazilCo AI search (natural-language property search via Claude, translating
   guest queries into the existing /properties/public/search filter params, or
   a more advanced semantic layer).
   REQUIRES: your Anthropic API key (env var, same pattern as every other secret),
   and a decision on whether Claude calls are synchronous (simple, blocks the
   request) or async via RabbitMQ (more complex, but matches the notification
   pattern) — recommend starting synchronous for simplicity given these are
   typically fast text-generation calls, not long-running jobs like the old image
   generation was.

## After Claude API phase, final remaining work

- MPESA + Products/Plans/Subscriptions (deferred, build last)
- Category 6 infrastructure: Docker Compose, Kubernetes manifests, CI/CD, Vault
- Frontend: only Module 1 (monorepo scaffold) exists. Modules 2-12 (theme, ui,
  types, api-client, auth, validation, and all 5 actual Next.js apps) are
  entirely unbuilt — there is currently NO UI.

## How to verify the backend still builds

cd C:\Users\YourUsername\Desktop\hostflow-backend
mvn clean install -DskipITs -Dtest='!\*IT' (unit tests, no Docker needed)
mvn clean install (full build incl. Testcontainers, needs Docker)

## Local services required (Docker, manual commands — no compose file yet)

- Postgres: pgvector/pgvector:pg16 image (NOT plain postgres — pgvector extension needed)
- Redis: redis:7-alpine
- RabbitMQ: rabbitmq:3.13-management-alpine (mgmt UI localhost:15672)
- Keycloak: quay.io/keycloak/keycloak:26.0, realm "hostflow" must be imported
- MinIO: minio/minio, bucket must be created manually matching STORAGE_BUCKET env var
