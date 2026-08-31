# Credit Bladi

[![Backend CI/CD](https://github.com/LouziAmine/---Credit-Bladi---/actions/workflows/ci-cd.yml/badge.svg?branch=release%2F2.1.0)](https://github.com/LouziAmine/---Credit-Bladi---/actions/workflows/ci-cd.yml)
![Java](https://img.shields.io/badge/Java-25-blue?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen?logo=springboot&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)

The API of Credit Bladi, a Moroccan credit platform built on Bank Al-Maghrib (BAM) rules. It covers public
simulations (individual mortgage, conventional or Mourabaha, and company investment loans), the bank's
instruction workflow up to the committee decision, the credit file after approval (schedule, payments, late
interest, early repayment) and the client portal. The web front end lives in
[---Bladi-Credit-Portal---](https://github.com/LouziAmine/---Bladi-Credit-Portal---).

> MVP. What is not built yet is listed in [Known limitations](docs/architecture.md#known-limitations).

## Quick Start

| Tool | Version | Notes |
|---|---|---|
| Java | 25 | Maven is not needed: `./mvnw` downloads it |
| Docker | Compose v2.24+ | PostgreSQL, ClamAV and Mailpit, or the whole stack |
| Node.js | 22.22+ or 24.15+ | Only for the front end |

The `dev` profile needs no configuration file: a fresh clone starts as is.

**Everything in Docker** (Linux, macOS, Windows). Clone both repositories side by side:

```bash
git clone https://github.com/LouziAmine/---Credit-Bladi---.git
git clone https://github.com/LouziAmine/---Bladi-Credit-Portal---.git
cd ./---Credit-Bladi---
docker compose --profile app up -d --build
```

The first build downloads every dependency and ClamAV's virus definitions: allow several minutes and about
3 GB of free memory.

**Apps on the machine** (faster to iterate):

```bash
docker compose up -d                                   # PostgreSQL, ClamAV, Mailpit
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev  # API on :8080

cd ../---Bladi-Credit-Portal---
npm ci && npm start                                    # portal on :4200
```

| | URL |
|---|---|
| Portal | `http://localhost:4200` |
| Swagger UI | `http://localhost:8080/swagger/credit-bladi/v3/swagger-ui.html` |
| Health | `http://localhost:8080/actuator/health` |
| Mailpit (e-mails and SMS in dev) | `http://localhost:8025` |
| PostgreSQL | `localhost:5432`, database `bladidb`, user and password `bladi` |

Demo login: `admin` / `admin123`. On the `dev` profile the database is rebuilt from scratch on every start
(schema, reference data, demo users).

### Troubleshooting

| Problem | Cause | Fix |
|---|---|---|
| `release version 25 not supported` | Java older than 25 | Install JDK 25 (`java -version`) |
| `Port 8080 was already in use` | Another app on 8080 | Stop it, or add `-Dspring-boot.run.arguments=--server.port=8081` and point the front end's `apiUrl` to it |
| `Connection to localhost:5432 refused` | PostgreSQL not started | `docker compose up -d`, then wait for `docker compose ps` to show it `healthy` |
| Upload refused, antivirus unavailable | ClamAV is still loading its definitions | Wait a few minutes after the first start |
| No e-mail or SMS | In dev they go to Mailpit | Open `http://localhost:8025` |
| Docker build killed | Not enough memory | Give Docker at least 4 GB |
| Staging or prod exits at startup | A required variable is missing | The log names it; see [Configuration](#configuration) |

## Documentation

| Document | For | Content |
|---|---|---|
| [Architecture at a Glance](docs/c4-architecture.md) | New developers, start here | System context, containers, back and front layers, [dependency graph](docs/dependency-graph.md) |
| [Feature Flows](docs/flows/README.md) | Developers | One sequence diagram per feature, from the click to the database and back |
| [Architecture](docs/architecture.md) | Developers | Transactions, package map, DDD, patterns, banking safeguards, known limitations |
| [Adding a Feature](docs/adding-a-feature.md) | Developers | Checklist for a new feature, on a real example |
| [API](docs/api.md) | Integrators | Endpoints, access rules, error format |
| [End-to-End Journeys](docs/end-to-end-flows.md) | Everyone | Business journeys with every success and error case |
| [Mortgage Credit](docs/mortgage-credit-workflow.md) | Business | Conventional and Mourabaha, eligibility, CSP profiles, the mortgage workflow |
| [Enterprise Credit](docs/enterprise-credit-workflow.md) | Business | Enterprise types, eligibility, the instruction steps, committee, documents, credit file |
| [Glossary](docs/glossary.md) | Everyone | Business words and the code names behind them |
| [Testing](docs/testing.md) | Developers, QA | How to run each suite, current results, reports |
| [Deployment](docs/deployment.md) | DevOps | CI/CD pipeline, Docker images, environment variables |

## Tech stack

| | |
|---|---|
| Runtime | Java 25, Spring Boot 4.0.6 |
| Security | Spring Security, JWT (JJWT) in `HttpOnly` cookies |
| Persistence | Spring Data JPA, Liquibase, PostgreSQL 16 (H2 for the test suites) |
| Cache | Caffeine |
| Notifications | Server-Sent Events, e-mail (Spring Mail), SMS through a swappable gateway |
| Batch | Spring Batch and Apache POI (Excel import) |
| Documents | Apache Tika (content check), ClamAV (antivirus), AES-256-GCM encryption at rest |
| API docs | SpringDoc OpenAPI |
| Tests | JUnit 5, Mockito, ArchUnit, Karate, Gatling |
| Code style | Checkstyle (Google Java Style) |

## Configuration

| Profile | Database | Secrets | Swagger |
|---|---|---|---|
| `dev` | Local PostgreSQL from `docker-compose.yml` | Local-only defaults, overridable in `.env.local` | Public |
| `staging` | From environment variables | Required | Public |
| `prod` | From environment variables | Required | Restricted |

Spring reads `.env` and then `.env.local` at startup (copy `.env.template`). Staging and prod refuse to start
when one of these is missing:

| Variable | Notes |
|---|---|
| `JWT_SECRET` | `openssl rand -base64 32` |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL |
| `CORS_ALLOWED_ORIGIN` | Portal URL (prod) |
| `DOCUMENT_STORAGE_PATH`, `DOCUMENT_ENCRYPTION_KEY` | Where documents are written, and their AES key |
| `CLAMAV_HOST`, `CLAMAV_PORT` | Antivirus |
| `PAYMENT_BENEFICIARY_RIB` | The account clients pay into, a valid Moroccan RIB |
| `SMS_GATEWAY_PROVIDER` | `http` (real gateway) or `mailpit` |
| `BAM_VERIFICATION_PROVIDER` | `mock` until a real provider is connected |

Business settings with defaults: `MAX_ANNUAL_RATE` (legal ceiling, 14.00), `MAX_RATE_SIMULATION_BASIS`
(`NOMINAL` or `TEG`), `LATE_INTEREST_MARGIN_POINTS` (2.00), and the `SMTP_*` and `SMS_GATEWAY_*` settings in
`.env.template`. The deployment guide lists them all: [Deployment](docs/deployment.md).

## Logs

`src/main/resources/log4j2-spring.xml` writes every event to the console, to `logs/credit-bladi.log` (text) and
to `logs/credit-bladi.json` (one JSON object per line, for ELK or Loki). Files roll daily or at 10 MB into
`logs/archive/`. Every line of a request carries the same `requestId`, so one `grep` rebuilds a whole call.
E-mails, phones and RIBs are masked. A real simulation request:

```
[INFO ] [af68c0e5-...] 21:44:35,705 RequestLoggerInterceptor : INCOMING REQUEST : METHOD=POST, URI=/api/v1/mortgage-credit
[INFO ] [af68c0e5-...] 21:44:35,706 MortgageCreditService    : Simulate - amount=500000 MAD, duration=240, productType=CONVENTIONAL
[INFO ] [af68c0e5-...] 21:44:35,718 WorkflowEngineService    : createDemand - reference=DEM-42D28F5CF5BD, workflowId=3, firstStep=CLASSIFICATION_STEP
[DEBUG] [af68c0e5-...] 21:44:35,719 AuditLogRepository       : save - action=SIMULATION
[INFO ] [af68c0e5-...] 21:44:35,724 RequestLoggerInterceptor : OUTGOING RESPONSE : METHOD=POST, URI=/api/v1/mortgage-credit, STATUS=200
```

## Security

- Access and refresh tokens in `HttpOnly`, `SameSite=Strict` cookies (`Secure` outside dev). The access token
  lasts 24 h in dev and 15 min in staging and prod; the refresh token lasts 7 days and is rotated on every
  use, and a reused refresh token revokes every session of the user.
- Tokens are stored hashed; logged-out access tokens are kept on a revocation list.
- Authorities are reloaded from the database on every request; the session is stateless.
- Rate limits on login, register, refresh, logout and both simulations; an account is locked for a while
  after repeated failed logins.
- Four-eyes rule on committee decisions, grade and team per step, no self-promotion, mandatory audit of
  every credit decision, payment, parameter, role and assignment change.
- Personal data masked in logs; uploaded documents scanned by ClamAV and encrypted at rest.

## Development

`./run-tests.sh` is an interactive menu over the usual commands: unit tests, Karate, Gatling, Checkstyle,
starting the app (with or without remote debug on `:5005`), Dependency-Check and ZAP. Details in
[Testing](docs/testing.md).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and the [Code of Conduct](CODE_OF_CONDUCT.md).

## License

[Apache License 2.0](LICENSE). Contact: Amine Louzi (louzi.amine.pro.pro@gmail.com).
