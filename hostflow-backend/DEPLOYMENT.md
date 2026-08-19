# Deployment Guide

Two independent deployment targets: the 5 Next.js apps go to **Vercel**,
the backend (Spring Boot `app` + `gateway-service`) and its infra
(Postgres, Redis, RabbitMQ, Keycloak, MinIO) go to **Docker** on whatever
host you pick. Neither depends on how the other is hosted, as long as the
env vars on each side point at the other's real public URL.

## 1. Backend — Docker

```
cd hostflow-backend
cp .env.prod.example .env.prod
# fill in every value in .env.prod — see comments in the file itself
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d --build
```

This builds `app` (port 8080 internally) and `gateway-service` (exposed on
`GATEWAY_PUBLIC_PORT`, default 8085) from their own Dockerfiles, alongside
Postgres/Redis/RabbitMQ/Keycloak/MinIO. **Not the same compose project as
local dev** (`docker-compose.yml` / project `hfdev`) — this is a separate
file with real env-var-driven credentials and no dev-only services
(MailHog, hardcoded passwords).

Before exposing this publicly:

1. **Rotate every Keycloak client secret.** The realm import
   (`modules/core-security/.../hostflow-realm.json`) ships with
   `dev-secret-<clientId>` secrets committed to the repo — importing it
   verbatim and going live with those secrets in place is a real
   vulnerability. Rotate each of the 6 clients' secrets via the Keycloak
   admin console (or `kcadm.sh`) immediately after first import, then
   update `KEYCLOAK_ADMIN_CLIENT_SECRET` in `.env.prod` and each frontend
   app's `KEYCLOAK_CLIENT_SECRET` in Vercel to match.
2. Put a reverse proxy (nginx/Caddy/your cloud LB) in front of
   `gateway-service` and `keycloak` for TLS termination — neither
   container does TLS itself. `KC_PROXY_HEADERS=xforwarded` is already set
   for Keycloak so it trusts `X-Forwarded-*` from a proxy in front of it.
3. Real SMTP/SMS/push credentials — see `PROJECT_STATE.md` gap #6.
   `SmsDeliveryService`/`PushDeliveryService`/`WhatsAppDeliveryService`
   are already correct, real integrations; they just need real accounts.

## 2. Frontend — Vercel (one project per app)

For **each** of `hostflow-web`, `nazilco-web`, `hostflow-admin`,
`nazilco-admin`, `xanuos-console`:

1. Create a new Vercel project from this repo.
2. Set **Root Directory** to `apps/<app-name>` (e.g. `apps/nazilco-web`).
   Vercel auto-detects Next.js from there; the app's own `vercel.json`
   handles the pnpm/Turborepo install+build commands.
3. Set environment variables (Project Settings → Environment Variables) —
   same keys as each app's `.env.local`, but with real values:

   | Variable | Value |
   |---|---|
   | `NEXT_PUBLIC_GATEWAY_URL` | `https://<your-gateway-domain>/api/v1` |
   | `KEYCLOAK_ISSUER` | `https://<your-keycloak-domain>/realms/hostflow` |
   | `KEYCLOAK_CLIENT_ID` | the app's client id (e.g. `nazilco-web`) |
   | `KEYCLOAK_CLIENT_SECRET` | the *rotated* secret for that client |
   | `SESSION_SECRET` | a real random 32+ char value — **not** the dev placeholder |
   | `APP_BASE_URL` | this app's real Vercel URL/custom domain |
   | `DEV_MOCK_AUTH` | `false` (or omit entirely) |

4. Deploy. Once you have the real Vercel URLs, go back to the backend's
   `.env.prod` and set `CORS_ORIGIN_1`..`CORS_ORIGIN_5` to match, then
   restart the `app` container so CORS actually allows these origins.

**Note on local build verification**: this dev machine doesn't have enough
free RAM to complete a `next build` locally (repeatedly OOM-killed
mid-compile, confirmed across 3 attempts). This does not block Vercel —
Vercel builds run on their own infrastructure, not this machine. What *was*
verified locally: clean `tsc --noEmit`, every page rendering correctly via
`next dev`, and `docker compose config` validating the compose file
structure.

## 3. First real login checklist

- The real seeded XanuOS owner: `owner@hostflow.dev` / `DevPass123!`
  (created via `seed-kenya-data.ps1` against local dev — re-run an
  equivalent onboarding against the production API once it's live).
- The real platform admin: `platformadmin@hostflow.dev` / `DevPass123!`,
  realm role `platform_admin`, `product_scope=[XANUOS, NAZILCO]`.
- **Change both passwords** before going live — these are dev-seeded
  values.
