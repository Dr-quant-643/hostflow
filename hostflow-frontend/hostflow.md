# HostFlow — Full Project Context & Completion Guide

**Purpose of this file:** This is the single source of truth for finishing HostFlow. It merges the original frontend build plan with the backend's final reconciliation report. Read this whole file before touching code. It tells you what's built, what's assumed-but-wrong, what's newly available, and exactly what's left to do to ship.

---

## 1. Project Identity

**HostFlow** — AI-powered PropTech/HospitalityTech ecosystem, parent brand over two products:

- **XanuOS** — property/business operations platform (owners, managers, staff) — B2B
- **NazilCo** — customer-facing booking/discovery platform (guests, travelers) — B2C, cross-tenant

**Repository layout (Desktop, sibling directories):**

```
hostflow-backend\    (COMPLETE — Java/Spring Boot)
hostflow-frontend\   (SCAFFOLDED — all 12 modules built, needs reconciliation + real endpoint wiring)
```

**Tools:** Apache Maven 3.9.16, Java 21.0.10, Git Bash, Git, VS Code, Node/pnpm, Claude Code.

**Working conventions carried through the whole build:**

- Divide work into modules; within large modules, into phases.
- Build foundational (non-independent) modules before independent ones.
- BigDecimal (never double/float) for all money/scoring values — serialized as `string` over the wire, typed `string` in TypeScript.
- Full professional SDLC discipline: report after each unit of work (purpose, files, tests, Definition of Done, known issues, next).

---

## 2. Backend — STATUS: COMPLETE (with several modules added since the original 15)

### 2.1 Original 15 modules (Spring Boot modular monolith, Java 21, Spring Boot 3.3.4)

```
hostflow-backend\
├── pom.xml (parent, Java 21, Spring Boot 3.3.4)
├── modules\
│   ├── core-common\          (exceptions, ApiResponse<T>, PageResponse<T>)
│   ├── core-config\          (GlobalExceptionHandler, CORS, Jackson)
│   ├── core-persistence\     (BaseEntity, Flyway roles/RLS function)
│   ├── core-tenancy\         (TenantContext, TenantScopedEntity, SET LOCAL wiring)
│   ├── core-security\        (Keycloak resource server, JWT converter, product scope)
│   ├── module-identity\      (Organization, User, Keycloak provisioning)
│   ├── module-property\      (Property, PropertyDocument, pgvector embeddings)
│   ├── module-booking\       (Booking, overlap detection, lifecycle)
│   ├── module-notification\  (Templates, RabbitMQ pipeline #1)
│   ├── module-crm\           (Contact, Interaction, lead lifecycle, SupportTicket)
│   ├── module-marketing\     (Campaign — manual content, no AI)
│   ├── module-billing\       (Invoice, Payment, Expense, Budget, batch API)
│   └── module-analytics\     (materialized views, occupancy/revenue reports)
├── gateway-service\          (Spring Cloud Gateway, standalone, port 8085)
└── app\                      (bootstrap Spring Boot app, port 8080)
```

### 2.2 Modules added since the original 15 (confirmed real, in production)

- **Maintenance** (work orders, assets, preventive maintenance schedules — auto-generates work orders daily when due)
- **Rental Management** (RentalTenant, leases, rent-payment schedules, activate/terminate)
- **Office Management** (meeting rooms, room bookings, visitor check-in/out)
- **Mall Management** (retail units, tenant assignment, mall events, parking entry/exit)
- **Reviews** (property reviews, owner responses)
- **My-Organization self-service** (owner/manager can manage their own staff without platform-admin — closes a real access gap)
- **Guest identity** (tenant-less guest registration, separate from staff `User`)
- **SupportTicket** — real entity with full lifecycle (assign/resolve/reopen/close), shared by both admin apps and both products via `productScope`
- **Feature flags, audit log, cross-product monitoring** (platform-console-only)

### 2.3 Architecture (locked decisions)

- **Auth:** Keycloak (OAuth2.1/OIDC), Spring Security Resource Server, JWT in httpOnly cookies via Next.js BFF pattern — raw JWT never reaches browser JS.
- **DB:** PostgreSQL + Row-Level Security for multi-tenancy, pgvector, Redis (cache/sessions).
- **Messaging:** RabbitMQ, per-module dedicated topology.
- **Multi-tenancy:** shared DB + `tenant_id` column + Postgres RLS; product scope (XanuOS/NazilCo) as Keycloak claim + Spring authority (`PRODUCT_XANUOS`/`PRODUCT_NAZILCO`).
- **Cross-tenant admin reads:** generalized `platformAdminJdbcTemplate` pattern (BYPASSRLS), used consistently everywhere `ROLE_PLATFORM_ADMIN` needs to read across tenants. **This resolves what was previously "Open Item A."**
- **Storage:** MinIO (local) / S3 (prod), objectKey-only references. Property photos/documents are served as **signed URLs that expire in 1 hour** — any screen showing them for longer needs to refetch.
- **Coding standard:** BigDecimal (never double/float) for all money/scoring values, string over the wire.

### 2.4 JWT claims to expect

- `tenant_id` — present for XanuOS staff, **absent for NazilCo guests** (intentional, not a bug — don't build frontend logic assuming every logged-in user has a tenant).
- `product_scope` — array, e.g. `["NAZILCO"]`.
- `realm_access.roles`.

### 2.5 Local dev services (Docker)

- PostgreSQL `localhost:5432` (`pgvector/pgvector:pg16` image — required)
- Redis `localhost:6379`
- RabbitMQ `localhost:5672` (management UI `15672`)
- Keycloak `localhost:8081` (issuer `http://localhost:8081/realms/hostflow`)

Run: `cd hostflow-backend && mvn -pl app -am spring-boot:run -Dspring-boot.run.profiles=dev`
Gateway: `cd hostflow-backend/gateway-service && mvn clean spring-boot:run`
Verify: `mvn clean install -DskipITs -Dtest='!*IT'` (unit) or `mvn clean install` (full, needs Docker)

---

## 3. Frontend — STATUS: ALL 12 MODULES SCAFFOLDED (built against assumed routes — this file corrects those assumptions)

**Architecture (locked decisions):** Next.js + React + TypeScript + Tailwind CSS + Shadcn/UI + Zustand + TanStack Query + React Hook Form + Zod. Turborepo monorepo, pnpm workspaces. Maps: Mapbox + Google Places. Deployment target: Docker + Kubernetes.

### 3.1 Foundation packages (Modules 1–7) — DONE, no known issues

```
packages\
├── theme\        (colors, semantic tokens, typography, radius, shadows, breakpoints, motion, z-index)
├── ui\            (Shadcn-based component library — Foundation/Layout/Navigation/Data/Feedback/Domain, 4 phases)
├── types\         (ApiResponse<T>, PageResponse<T>, AuthClaims, domain DTOs — money fields typed `string`)
├── api-client\    (apiFetch + api.{get,post,patch,put,delete}, envelope-unwrapping, retry/backoff)
├── auth\          (PKCE, encrypted httpOnly session cookie, Keycloak token exchange, route handlers)
└── validation\    (Zod schemas per domain, decoupled from `types`)
```

### 3.2 Apps (Modules 8–12) — SCAFFOLDED, need route/shape reconciliation per §4–§9 below

```
apps\
├── hostflow-web\        (XanuOS main app — port 3000)
├── nazilco-web\         (NazilCo customer app — port 3001)
├── hostflow-admin\      (XanuOS support/billing admin — port 3002)
├── nazilco-admin\       (NazilCo support/booking admin — port 3003)
└── xanuos-console\      (platform-wide super-admin — port 3004)
```

Each app has: its own `package.json`/`tsconfig`/`tailwind.config`/`next.config`, its own Keycloak client, its own middleware (auth-gating pattern differs — see below), a Shell (`(shell)` route group with Sidebar/Topbar), and route-group-scoped pages.

**Middleware auth-gating pattern per app (as built):**

- `hostflow-web`, `hostflow-admin`, `nazilco-admin`, `xanuos-console`: **protect-by-default** — everything requires a session except `/login`, `/api/auth/*`.
- `nazilco-web`: **public-by-default** — only `/checkout`, `/guest-portal`, `/profile` require a session. (⚠️ gap: `/properties/*/book` should probably also be protected — see §10.)

**⚠️ Known frontend-side gap, unresolved by the backend reconciliation, still needs fixing:** every app's middleware only checks **session-cookie presence**, never actual authority (`PRODUCT_XANUOS`/`PRODUCT_NAZILCO`/`ROLE_PLATFORM_ADMIN`). This needs server-side `hasAuthority()` checks added per protected route/layout — most urgent in `xanuos-console`, where every route should require `ROLE_PLATFORM_ADMIN` specifically.

---

## 4. How to Read the Reconciliation Tables (§5–§9)

| Symbol     | Meaning                                                                                   |
| ---------- | ----------------------------------------------------------------------------------------- |
| ✅ KEEP    | Frontend assumption was correct — endpoint exists and works as originally built           |
| 🔧 UPDATE  | Endpoint exists but frontend needs to change something (route, shape, new required field) |
| ➕ ADD     | New endpoint/screen the frontend didn't know about — now available, needs building        |
| ❌ REMOVE  | Frontend built against a capability that has been **permanently deleted** — remove it     |
| 🚩 FLAGGED | Known backend gap — build the screen defensively, or wait, per the note                   |

---

## 5. NazilCo (`nazilco-web`) — Guest-Facing

### Discovery & Booking

| Screen                          | Route                                                                                         | Auth        | Status                                                                  |
| ------------------------------- | --------------------------------------------------------------------------------------------- | ----------- | ----------------------------------------------------------------------- |
| Discover (list)                 | `GET /api/v1/properties/public?limit=&offset=`                                                | None        | ➕ ADD                                                                  |
| Search                          | `GET /api/v1/properties/public/search?city=&propertyType=&minPrice=&maxPrice=&limit=&offset=` | None        | ➕ ADD                                                                  |
| Property Details                | `GET /api/v1/properties/public/{id}`                                                          | None        | ➕ ADD                                                                  |
| Property photo gallery          | `GET /api/v1/properties/public/{id}/photos`                                                   | None        | ➕ ADD — signed URLs, **expire in 1 hour**, refetch on long-lived pages |
| Availability check              | `GET /api/v1/bookings/public/availability?propertyId=&checkIn=&checkOut=`                     | None        | ➕ ADD                                                                  |
| Sign up                         | `POST /api/v1/guests/register`                                                                | None        | ➕ ADD — creates a tenant-less guest identity                           |
| Create booking                  | `POST /api/v1/bookings/public`                                                                | Guest login | ➕ ADD                                                                  |
| My bookings                     | `GET /api/v1/bookings/public/mine`                                                            | Guest login | ➕ ADD                                                                  |
| Booking confirm (checkout step) | `PATCH /api/v1/bookings/public/{id}/confirm`                                                  | Guest login | ➕ ADD                                                                  |
| Booking cancel                  | `PATCH /api/v1/bookings/public/{id}/cancel`                                                   | Guest login | ➕ ADD                                                                  |
| Digital check-in (ID upload)    | `POST /api/v1/bookings/public/{bookingId}/digital-checkin` (multipart, field `idDocument`)    | Guest login | ➕ ADD                                                                  |

### Guest Dashboard / Account

| Screen               | Route                                           | Status                                                                         |
| -------------------- | ----------------------------------------------- | ------------------------------------------------------------------------------ |
| Unified dashboard    | `GET /api/v1/dashboard/mine`                    | ➕ ADD — aggregates upcoming bookings + recent invoices + recent notifications |
| My invoices (list)   | `GET /api/v1/invoices/mine`                     | ➕ ADD                                                                         |
| My invoices (detail) | `GET /api/v1/invoices/mine/{id}`                | ➕ ADD                                                                         |
| Notification inbox   | `GET /api/v1/notifications/mine?limit=&offset=` | ➕ ADD — shared endpoint, works for both guests and staff                      |

### Reviews

| Screen                         | Route                             | Status                                                                                                                                                                                     |
| ------------------------------ | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Property reviews (public read) | `GET /api/v1/reviews?propertyId=` | 🔧 **currently still requires auth** — backend needs one more `SecurityConfig` `permitAll` pass. Build the screen assuming it becomes public soon, but treat as authenticated-only for now |
| Leave a review                 | `POST /api/v1/reviews`            | ➕ ADD — only works if the booking is `CHECKED_OUT` and owned by the caller                                                                                                                |

### Rental Portal / Mall / Office (guest-facing)

| Screen               | Route                                                 | Status                                                                                                                                                                     |
| -------------------- | ----------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| My leases            | `GET /api/v1/rental/portal/my-leases`                 | 🚩 FLAGGED — will return empty for every real user; RentalTenant↔guest-account link isn't built. **Build the screen, expect it non-functional against real data for now.** |
| My rent schedule     | `GET /api/v1/rental/portal/my-rent-schedule?leaseId=` | 🚩 same gap                                                                                                                                                                |
| Mall store directory | `GET /api/v1/mall/public/store-directory?propertyId=` | ➕ ADD, public                                                                                                                                                             |
| Office room list     | `GET /api/v1/office/public/rooms?propertyId=`         | ➕ ADD, public                                                                                                                                                             |

### Support

| Screen         | Route                                                                                                                                                  | Status |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ | ------ |
| Raise a ticket | `POST /api/v1/crm/support-tickets` (set `productScope: NAZILCO`)                                                                                       | ➕ ADD |
| My tickets     | 🚩 **DO NOT BUILD YET** — list endpoint has no requester-filter; a naive "my tickets" screen would leak other users' tickets (real access-control bug) |

### ❌ Remove from `nazilco-web` if already scaffolded

- Any real payment/checkout **charge** flow — not built, checkout is confirm+invoice only
- Anything assuming MPESA is live

---

## 6. XanuOS (`hostflow-web`) — Owner/Manager/Staff

### Property Management

| Screen           | Route                                                                      | Status                                                                                                                                                                                 |
| ---------------- | -------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Create property  | `POST /api/v1/properties`                                                  | ✅ KEEP                                                                                                                                                                                |
| Publish property | `PATCH /api/v1/properties/{id}/publish`                                    | ✅ KEEP                                                                                                                                                                                |
| Archive property | `PATCH /api/v1/properties/{id}/archive`                                    | ➕ ADD — was missing before                                                                                                                                                            |
| Upload document  | `POST /api/v1/properties/{propertyId}/documents` (multipart, field `file`) | 🔧 UPDATE — **now requires `documentType`** param (`PHOTO`/`FLOOR_PLAN`/`CONTRACT`/`INSURANCE`/`OTHER`). If the frontend built photo-only upload without this param, **it will break** |
| Document list    | `GET /api/v1/properties/{propertyId}/documents?documentType=`              | 🔧 UPDATE — `documentType` filter is new, optional                                                                                                                                     |
| Delete document  | `DELETE /api/v1/properties/{propertyId}/documents/{documentId}`            | ✅ KEEP                                                                                                                                                                                |

### Team Management (self-service — new, closes a real access gap)

| Screen                | Route                                                     | Status                                                                       |
| --------------------- | --------------------------------------------------------- | ---------------------------------------------------------------------------- |
| My team (list/search) | `GET /api/v1/my-organization/users`, `.../search`         | ➕ ADD — owner/manager can now manage their own staff without platform-admin |
| Edit roles            | `PATCH /api/v1/my-organization/users/{userId}/roles`      | ➕ ADD — cannot grant `PLATFORM_ADMIN` through this path                     |
| Deactivate staff      | `PATCH /api/v1/my-organization/users/{userId}/deactivate` | ➕ ADD                                                                       |

### Bookings (staff-facing)

| Screen          | Route                                 | Status                                                   |
| --------------- | ------------------------------------- | -------------------------------------------------------- |
| Booking detail  | `GET /api/v1/bookings/{id}`           | ✅ KEEP                                                  |
| Confirm booking | `PATCH /api/v1/bookings/{id}/confirm` | ➕ ADD — was entirely missing from the controller before |
| Cancel booking  | `PATCH /api/v1/bookings/{id}/cancel`  | ✅ KEEP                                                  |

### CRM & Support

| Screen          | Route                                                     | Status  |
| --------------- | --------------------------------------------------------- | ------- |
| Contacts        | `POST/GET/PATCH /api/v1/crm/contacts`, `.../interactions` | ✅ KEEP |
| Support tickets | `POST/GET/PATCH /api/v1/crm/support-tickets/**`           | ✅ KEEP |

### Marketing — 🔧 SIGNIFICANT CHANGE

| Old assumption                                                                   | New reality                                                                                                            |
| -------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| `POST /marketing/campaigns/{id}/generate` (AI content generation)                | ❌ **REMOVED entirely** — AI content generation was fully deleted from the architecture                                |
| Campaign had a `prompt` field, `GENERATING`/`READY`/`GENERATION_FAILED` statuses | 🔧 UPDATE — Campaign now has a plain `content` field (manually written); status is only `DRAFT`/`PUBLISHED`/`ARCHIVED` |
| —                                                                                | ➕ NEW: `PATCH /api/v1/marketing/campaigns/{id}/content` — edit content directly                                       |
| —                                                                                | ➕ NEW: `PATCH /api/v1/marketing/campaigns/{id}/archive`                                                               |

**Action:** delete the `ContentGenerator` component and `useGenerateCampaignContent` hook entirely from `hostflow-web`. Replace with a plain textarea bound to `content` + a save action hitting `.../content`.

### Financial Management (fully new)

| Screen                                 | Route                                                   | Status                                                          |
| -------------------------------------- | ------------------------------------------------------- | --------------------------------------------------------------- |
| Record expense                         | `POST /api/v1/billing/expenses`                         | ➕ ADD                                                          |
| Expense list                           | `GET /api/v1/billing/expenses?propertyId=`              | ➕ ADD                                                          |
| Set budget                             | `PUT /api/v1/billing/budgets` (upsert)                  | ➕ ADD                                                          |
| Budget vs actual                       | `GET /api/v1/billing/budgets/variance?month=YYYY-MM-01` | ➕ ADD                                                          |
| Record payment                         | `POST /api/v1/billing/payments`                         | ➕ ADD — real gap closed (entity existed with no service layer) |
| Payments by invoice                    | `GET /api/v1/billing/payments?invoiceId=`               | ➕ ADD                                                          |
| Mark payment succeeded/failed/refunded | `PATCH .../payments/{id}/succeed\|fail\|refund`         | ➕ ADD                                                          |
| Batch invoice import                   | `POST /api/v1/billing/invoices/batch`                   | ✅ KEEP (≤100 rows, per-row success/failure report)             |

### Maintenance (fully new module)

| Screen                           | Route                                                           | Status                                             |
| -------------------------------- | --------------------------------------------------------------- | -------------------------------------------------- |
| Report issue                     | `POST /api/v1/maintenance/work-orders`                          | ➕ ADD                                             |
| Work order list                  | `GET /api/v1/maintenance/work-orders?propertyId=`               | ➕ ADD                                             |
| My assignments (technician)      | `GET /api/v1/maintenance/work-orders/my-assignments`            | ➕ ADD                                             |
| Assign/Start/Complete/Cancel     | `PATCH .../{id}/assign\|start\|complete\|cancel`                | ➕ ADD                                             |
| Assets                           | `POST/GET /api/v1/maintenance/assets`, `PATCH .../decommission` | ➕ ADD                                             |
| Preventive maintenance schedules | `POST /api/v1/maintenance/schedules`                            | ➕ ADD — auto-generates work orders daily when due |

### Rental Management (fully new module)

| Screen                                   | Route                                                      | Status |
| ---------------------------------------- | ---------------------------------------------------------- | ------ |
| Add tenant                               | `POST /api/v1/rental/tenants`                              | ➕ ADD |
| Create/list leases                       | `POST /api/v1/rental/leases`, `GET .../leases?propertyId=` | ➕ ADD |
| Activate lease (generates rent schedule) | `PATCH /api/v1/rental/leases/{id}/activate`                | ➕ ADD |
| Terminate lease                          | `PATCH /api/v1/rental/leases/{id}/terminate`               | ➕ ADD |
| Rent schedule                            | `GET /api/v1/rental/rent-payments?leaseId=`                | ➕ ADD |
| Mark rent paid/waived                    | `PATCH .../rent-payments/{id}/mark-paid\|waive`            | ➕ ADD |

### Office Management (fully new module)

| Screen               | Route                                                        | Status |
| -------------------- | ------------------------------------------------------------ | ------ |
| Meeting rooms        | `POST/GET /api/v1/office/rooms?propertyId=`                  | ➕ ADD |
| Book/cancel room     | `POST /api/v1/office/room-bookings`, `PATCH .../{id}/cancel` | ➕ ADD |
| Visitors             | `POST/GET /api/v1/office/visitors?propertyId=`               | ➕ ADD |
| Visitor check-in/out | `PATCH .../{id}/check-in\|check-out`                         | ➕ ADD |

### Mall Management (fully new module)

| Screen               | Route                                                    | Status |
| -------------------- | -------------------------------------------------------- | ------ |
| Retail units         | `POST/GET /api/v1/mall/retail-units?propertyId=`         | ➕ ADD |
| Assign retail tenant | `POST /api/v1/mall/retail-units/assign-tenant`           | ➕ ADD |
| Mall events          | `POST/GET /api/v1/mall/events?propertyId=`               | ➕ ADD |
| Parking entry/exit   | `POST /api/v1/mall/parking/enter`, `PATCH .../{id}/exit` | ➕ ADD |

### Reviews (owner side)

| Screen            | Route                                | Status |
| ----------------- | ------------------------------------ | ------ |
| Respond to review | `PATCH /api/v1/reviews/{id}/respond` | ➕ ADD |

---

## 7. `hostflow-admin` (XanuOS support/billing)

| Screen                         | Route                                                 | Status                                                                                                           |
| ------------------------------ | ----------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| Billing (cross-tenant)         | `GET /admin/billing/invoices?status=&limit=&offset=`  | ✅ KEEP, `PLATFORM_ADMIN` only — this is now real, backed by the generalized `platformAdminJdbcTemplate` pattern |
| Products/Plans                 | —                                                     | ❌ **REMOVE nav item** — not built, deferred to project end alongside MPESA                                      |
| Support tickets (XanuOS scope) | `GET /api/v1/crm/support-tickets?productScope=XANUOS` | ✅ KEEP — replaces the old CRM-Interaction-based Support screen entirely                                         |

**Action:** delete the old `use-admin-support.ts` hook (built on `/crm/interactions` filtered by an assumed `SUPPORT_REQUEST` type) and `use-admin-billing.ts`'s speculative `/admin/products/plans` reference. Rebuild Support against the real `SupportTicket` endpoints. Remove the Products nav item from `lib/nav-config.ts`.

---

## 8. `nazilco-admin` (NazilCo support/booking oversight)

| Screen                            | Route                                                  | Status                                                                              |
| --------------------------------- | ------------------------------------------------------ | ----------------------------------------------------------------------------------- |
| Bookings oversight (cross-tenant) | `GET /admin/bookings?status=&limit=&offset=`           | ✅ KEEP, `PLATFORM_ADMIN` only — real now, same `platformAdminJdbcTemplate` pattern |
| Support tickets (NazilCo scope)   | `GET /api/v1/crm/support-tickets?productScope=NAZILCO` | ✅ KEEP — replaces the CRM-Interaction-based Support screen                         |

**Action:** same as §7 — delete `use-nazilco-support.ts`'s Interaction-based approach, rebuild against `SupportTicket`.

---

## 9. `xanuos-console` (platform-wide super-admin)

| Screen                        | Route                                                                    | Status                                                                                                                  |
| ----------------------------- | ------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------- |
| Organizations                 | `GET /organizations?limit=&offset=`                                      | ✅ KEEP                                                                                                                 |
| Rename organization           | `PATCH /organizations/{orgId}/rename`                                    | ➕ ADD                                                                                                                  |
| Onboard new org               | `POST /organizations`                                                    | ✅ KEEP                                                                                                                 |
| Platform users                | `GET /admin/platform-users?limit=&offset=`                               | ✅ KEEP — route differs slightly from what was scaffolded (`/organizations/users/search`); update the hook              |
| Org user management (any org) | `GET/PATCH /organizations/{orgId}/users/**`                              | ✅ KEEP                                                                                                                 |
| System health                 | `GET /admin/health` — **call this on the Gateway, not the app directly** | 🔧 UPDATE — confirm/set the base URL the frontend points at; this resolves the previously-stubbed Health page           |
| Feature flags                 | `GET/PUT /api/v1/admin/feature-flags`, `PUT .../org/{orgId}`             | ➕ ADD — this is the real content for what was the "Global Config" stub                                                 |
| Audit log                     | `GET /api/v1/admin/audit-log?tenantId=&limit=&offset=`                   | ➕ ADD                                                                                                                  |
| Cross-product monitoring      | `GET /api/v1/admin/monitoring`                                           | ➕ ADD — basic counts only (orgs, users, active bookings, open tickets, open work orders), not a full metrics dashboard |
| Products/Plans                | —                                                                        | ❌ **REMOVE nav item for now** — deferred with MPESA                                                                    |

**Action:** replace the honest "not yet wired" stubs on System Health and Global Config with real implementations against `/admin/health`, `/api/v1/admin/feature-flags`, `/api/v1/admin/audit-log`, `/api/v1/admin/monitoring`. Remove Products nav item.

---

## 10. Fully Deleted Backend Capabilities — Remove Any Frontend Trace

If scaffolded, **delete these**:

- ❌ AI content generation for marketing campaigns — endpoint, `prompt` field, generation status states all gone (`ContentGenerator` component, `useGenerateCampaignContent` hook in `hostflow-web`)
- ❌ AI WhatsApp "assistant" as a conversational AI concept — gone (real WhatsApp message **delivery** as a notification channel still exists and is valid — don't confuse the two)
- ❌ Any assumption of a FastAPI-backed AI content service — never existed in production, fully removed from the architecture

---

## 11. Authentication Setup (applies to all 5 apps)

- Keycloak clients: `hostflow-web`, `nazilco-web`, `hostflow-admin`, `nazilco-admin`, `xanuos-console` — each needs its own client secret in that app's `.env.local`.
- Flow: Authorization Code + PKCE, via the Next.js BFF pattern already built in `packages/auth` (tokens server-side only, httpOnly cookies, never exposed to client JS).
- JWT claims: `tenant_id` (absent for guests — expected), `product_scope` (array), `realm_access.roles`.

**🚩 ACTION NEEDED FROM YOU (not a code task):** the realm export now has all 6 clients defined in code, but confirm the realm was actually **re-imported into your running Keycloak** and that secrets were generated and dropped into each app's `.env.local`. This was the single blocker preventing any real login testing across all 5 apps — resolve this before attempting end-to-end testing.

---

## 12. Cross-Cutting Implementation Notes

- **Signed URLs expire in 1 hour.** Any screen displaying property photos/documents for longer than that needs to refetch, or images will 403.
- **Guest JWTs have no `tenant_id`.** Intentional. Don't gate NazilCo guest-facing logic on tenant presence.
- **SupportTicket list access-control gap:** do not build a "my tickets only" guest view yet — backend doesn't filter by requester correctly. Real bug, not a nice-to-have.
- **Push notifications:** FCM delivery mechanism exists, but there's no device-token registration. Don't build push opt-in UI yet — there's nowhere for a token to go.
- **Rental Portal will show empty data for real users** until the tenant-to-account linking workflow is built. Build the screen; expect it non-functional against real data for now.
- **Reviews public-read** is not yet truly public despite intent — treat as authenticated-only until the backend's `permitAll` pass lands.

---

## 13. Currently Open / Flagged Backend Issues (not frontend-fixable — track, don't route around)

1. Rental-tenant-to-guest-account linking — not built, blocks Rental Portal
2. No standard notification templates auto-created on org onboarding (e.g. `booking_confirmed` may not exist for a given org — that auto-email silently no-ops)
3. No device-token registration for push notifications
4. `SupportTicket` list endpoint lacks requester-based filtering (access-control gap)
5. `GET /api/v1/reviews` not yet truly public (still requires auth despite intent)
6. Keycloak realm re-import + client secret distribution — needs manual confirmation (see §11)
7. Real provider credentials not yet supplied: SMTP, Africa's Talking (SMS), FCM (push), WhatsApp Business Cloud API
8. Docker Compose / CI-CD / Vault — not started (infrastructure phase, after AI integration)
9. Claude API integration (dynamic pricing, recommendations, AI search, marketing analytics) — not started, next _backend_ phase, not frontend's concern yet
10. MPESA + Products/Plans/Subscriptions — deliberately deferred to the very end
11. Virtual tours, device management, coworking/desk booking — consciously deferred, awaiting a build-or-skip decision

---

## 14. What NOT to Build Yet (frontend)

- Real payment/checkout **charging** (MPESA not built) — checkout stays confirm-and-invoice only
- Products/Plans/subscription selection screens (remove nav items in `hostflow-admin` and `xanuos-console`)
- Any AI-powered feature — pricing suggestions, recommendations, AI search, marketing content generation, marketing insights — **none of this exists on the backend**, all prior frontend assumptions of this are deleted
- Push notification opt-in (no device-token storage)
- "My tickets" filtered guest view (access-control gap — would leak data)
- Rental Portal should be built but is _expected_ to render empty against real data for now

---

## 15. Consolidated Action Plan — What's Left to Finish the Project

This is the actual punch list. Work top to bottom.

### 15.1 Immediate reconciliation work (fix what's already built)

- [ ] **Marketing:** delete `ContentGenerator` + `useGenerateCampaignContent`; rebuild Campaign edit around plain `content` field + `DRAFT`/`PUBLISHED`/`ARCHIVED` status only; add archive action.
- [ ] **Property documents:** add required `documentType` param to the upload hook/form; add optional filter to the document list.
- [ ] **Support (both admin apps):** delete Interaction-based Support hooks/components entirely; rebuild against real `SupportTicket` CRUD + lifecycle (assign/resolve/reopen/close), scoped by `productScope`.
- [ ] **Billing/Bookings admin (both admin apps):** point at the now-real `/admin/billing/invoices` and `/admin/bookings` — remove the "isn't available yet" empty-state framing, these work now.
- [ ] **Products nav items:** remove from `hostflow-admin` and `xanuos-console` nav configs entirely (or mark "coming soon" if you'd rather keep the slot).
- [ ] **`xanuos-console` System Health:** wire to real `GET /admin/health` via the Gateway base URL — replace the stub.
- [ ] **`xanuos-console` Global Config:** replace stub with real Feature Flags (`GET/PUT /api/v1/admin/feature-flags`) — this is the actual backing entity, not a generic "config."
- [ ] **`xanuos-console` Platform Users:** fix hook to call `GET /admin/platform-users`, not the previously-assumed `/organizations/users/search`.
- [ ] Add `hasAuthority()` server-side gating to every protected route/layout in all 5 apps (this was never resolved — it's a pure frontend gap, not covered by the backend reconciliation).

### 15.2 New screens to build (net-new, not corrections)

- [ ] `nazilco-web`: Discover, Search, Property Details + photo gallery, Availability, Guest signup/login, Create/view/confirm/cancel booking, Digital check-in, Unified guest dashboard, My invoices, Notification inbox, Reviews (read + leave), Mall directory, Office room list (public), Raise-a-ticket
- [ ] `hostflow-web`: Archive property, My Team (list/search/edit-roles/deactivate), Confirm booking action, full Financial Management (expenses/budgets/payments), full Maintenance module, full Rental Management module, full Office Management module, full Mall Management module, Review response
- [ ] `hostflow-admin`: rebuilt Support (SupportTicket-based)
- [ ] `nazilco-admin`: rebuilt Support (SupportTicket-based)
- [ ] `xanuos-console`: Rename organization, Audit log, Cross-product monitoring, Feature flags

### 15.3 Infrastructure / non-code action items

- [ ] Confirm Keycloak realm re-imported + all 6 client secrets generated and placed in each app's `.env.local`
- [ ] De-duplicate `Providers`/root `layout.tsx`/auth-route re-exports across all 5 apps into a shared package (optional but recommended cleanup — not blocking)
- [ ] Consolidate the 3+ duplicate `/bookings/{id}` fetch hooks into one shared `api-client` hook (optional cleanup)
- [ ] Reconcile `hostflow-web/package.json` to include `@hookform/resolvers`/`papaparse` from the start (currently added mid-build, needs a clean rewrite)
- [ ] Decide: gate `nazilco-web`'s `/properties/[id]/book` route in middleware, or confirm guest-checkout-without-prior-account is truly the intended flow (now partially answered — guest signup exists via `/api/v1/guests/register`, so booking likely **should** require guest login first; update `PROTECTED_PATHS` accordingly)

### 15.4 Explicitly deferred — do not build now

- MPESA payment charging, Products/Plans/subscriptions (backend deferred to project end)
- Any AI-powered feature (backend hasn't started Claude API integration yet)
- Push notification opt-in UI
- "My tickets" guest-filtered view

---

## 16. Suggested Build Order From Here

1. Fix the reconciliation items (§15.1) across all 5 apps — this makes the existing scaffolding actually work.
2. Confirm Keycloak (§15.3, infra) — unblocks all real end-to-end testing.
3. Build `nazilco-web`'s missing guest-facing screens (§15.2) — this is the product's actual customer-facing surface and was the least-grounded app before this reconciliation.
4. Build `hostflow-web`'s new operational modules (Maintenance, Rental, Office, Mall, Financial) — largest remaining scope, but each is a straightforward CRUD pattern already established (list/detail/form + TanStack Query hooks, matching Properties/Bookings/CRM's existing shape).
5. Rebuild both admin apps' Support screens against real `SupportTicket`.
6. Finish `xanuos-console`'s remaining real screens (Feature Flags, Audit Log, Monitoring).
7. Full Integration pass → Docker Compose → Kubernetes → Production.
8. Return to backend's remaining open items (§13) as their own phase: MPESA, Claude API integration, provider credentials, Rental-tenant-account linking, notification template seeding.

---

_End of context file. This document supersedes all prior per-module "known issues" notes for anything listed as ✅/🔧/➕/❌ above — those are resolved. Items marked 🚩 or listed in §13/§14 remain genuinely open and should be tracked as backend work, not routed around in the frontend._
