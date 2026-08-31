# CI/CD & Deployment

Credit Bladi ships as two container images, built and published by GitHub Actions:

| Image | Repository | Contents |
|---|---|---|
| `ghcr.io/<owner>/credit-bladi-api` | this repo (`.github/workflows/ci-cd.yml`) | Spring Boot jar on `eclipse-temurin:25-jre`, unprivileged user, port `8080` |
| `ghcr.io/<owner>/credit-bladi-portal` | [frontend repo](https://github.com/LouziAmine/---Bladi-Credit-Portal---) (`.github/workflows/ci-cd.yml`) | Production Angular build on unprivileged nginx, port `8080`, reverse-proxies `/api/` to the backend |

The browser only talks to the portal: nginx serves the SPA and forwards `/api/` to the API. The auth
cookies stay `SameSite=Strict` and no CORS is involved in production.

## Pipeline

| Job | Runs on | What it does |
|---|---|---|
| `build-test` | every push and pull request | `./mvnw verify`, which runs the unit tests and the Karate integration tests (H2, no external service) with JaCoCo coverage. Then `checkstyle:check`. The jar and the test/coverage reports are uploaded as workflow artifacts. |
| `docker` | pushes to `master`/`main`/`release/**` and `v*` tags (not PRs) | Builds the image and pushes it to GitHub Container Registry. Tags: branch name, `sha-<commit>`, `x.y.z` for a `vx.y.z` tag, `latest` on the default branch. |
| `deploy` | after `docker`, **only when the repository variable `DEPLOY_ENABLED` is `true`** | Calls the platform's deploy hook with the new image reference, then optionally polls a health URL. Branch pushes deploy to the `staging` environment and `v*` tags deploy to `production`. |

The frontend repository has the same three jobs. Its `build-test` job runs lint, Vitest with coverage
thresholds, the dependency audit and the production build.

## Turning on automatic deployment

Nothing is deployed until you opt in, so the pipeline stays green on a fork or without a cloud account.

1. **Repository → Settings → Environments**: create `staging` and `production`. Add required reviewers on
   `production` if releases need a manual approval.
2. In each environment, add the **secret** `DEPLOY_HOOK_URL`. This is the deploy webhook of the hosting
   platform: Render, Railway, Koyeb, Coolify, Dokploy, Portainer and CapRover all provide one. The job
   POSTs `{"image", "ref", "sha"}` to it.
3. Optional **variables** per environment:
   - `HEALTHCHECK_URL` (backend) or `APP_URL` (frontend): checked for up to 5 minutes after the deploy.
     For the API, use `https://<api-host>/actuator/health`.
4. **Repository → Settings → Variables**: set `DEPLOY_ENABLED=true`.
5. The images are private by default on ghcr.io. Either make the packages public, or give the platform
   a registry token (a GitHub PAT with `read:packages`).

## Runtime configuration

### Backend (`SPRING_PROFILES_ACTIVE=prod`)

Startup **fails** when a required variable is missing. There is no silent fallback to a demo value.

| Variable | Required | Notes |
|---|---|---|
| `JWT_SECRET` | yes | `openssl rand -base64 32` |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | yes | PostgreSQL 16, e.g. `jdbc:postgresql://host:5432/bladidb`; the schema is created by Liquibase |
| `CORS_ALLOWED_ORIGIN` | yes | The portal's public URL |
| `DOCUMENT_STORAGE_PATH` | yes | Mount a persistent volume here (uploaded documents, encrypted) |
| `DOCUMENT_ENCRYPTION_KEY` | yes | `openssl rand -base64 32`; losing it makes stored documents unreadable |
| `CLAMAV_HOST`, `CLAMAV_PORT` | yes | A `clamav/clamav` container; uploads are refused when it is unreachable |
| `PAYMENT_BENEFICIARY_RIB` | yes | Valid Moroccan RIB, checked at startup |
| `SMS_GATEWAY_PROVIDER` | yes | `http` for a real gateway (`SMS_GATEWAY_*`), `mailpit` for a demo |
| `BAM_VERIFICATION_PROVIDER` | yes | `mock` until the real BAM/SCIP gateway is implemented |
| `SMTP_*` | for e-mails | See `.env.template` |

The container honours the memory limit (`-XX:MaxRAMPercentage=75`). Give it at least 1 GB.

### Frontend

| Variable | Default | Notes |
|---|---|---|
| `API_UPSTREAM` | `http://backend:8080` | Internal URL of the API container (no trailing path) |
| `NGINX_PORT` | `8080` | Port nginx listens on |

The nginx configuration (`deploy/nginx/default.conf.template` in the frontend repo) sets:
- a strict Content-Security-Policy, with a per-response nonce for Angular's inline styles;
- HSTS, `X-Frame-Options: DENY`, `nosniff` and a referrer policy;
- immutable caching for hashed bundles, and `no-cache` for `index.html` and the translation files;
- an SSE-friendly proxy (no buffering, 1 h read timeout) and a 16 MB upload limit.

Terminate TLS at the platform's load balancer.

## Local run with the images

`docker-compose.yml` builds both images and runs them with PostgreSQL, ClamAV and Mailpit (the backend
uses the `dev` profile and `.env.local`):

```bash
docker compose up -d --build
# portal: http://localhost:4200   (nginx, proxies /api/ to the backend on :8080)
```
