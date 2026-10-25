# Architecture at a Glance (C4)

Three zoom levels, from "who uses what" to "which layer calls which". For the step-by-step path of each feature
(click → Angular → API → database → response), see [Feature Flows](flows/README.md).

- Backend code: this repository.
- Frontend code: [---Bladi-Credit-Portal---](https://github.com/LouziAmine/---Bladi-Credit-Portal---).

## Level 1: System context

Who uses Credit Bladi, and which outside systems it talks to.

```mermaid
flowchart LR
    prospect(["Prospect / client<br/>(individual or company)"])
    staff(["Bank staff<br/>(MANAGER: analysts, committee, back office)"])
    system["<b>Credit Bladi</b><br/>credit simulation, instruction workflow,<br/>credit file servicing, client portal"]
    mail["E-mail relay<br/>(SMTP, Mailpit in dev)"]
    sms["SMS gateway<br/>(HTTP aggregator, Mailpit in dev)"]
    bam["BAM / SCIP / Centrale des Risques<br/>(mock until a provider is contracted)"]
    av["Antivirus<br/>(ClamAV)"]

    prospect -- "simulates, follows the dossier,<br/>uploads documents, declares payments" --> system
    staff -- "classifies, instructs, decides,<br/>validates payments, sets parameters" --> system
    system -- "notifications, invitations" --> mail
    system -- "notifications" --> sms
    system -- "borrower checks" --> bam
    system -- "scans every upload" --> av
```

## Level 2: Containers

What runs, and how the pieces talk to each other.

```mermaid
flowchart LR
    browser(["Browser"])
    subgraph portal["Portal container (nginx)"]
        spa["Angular 22 SPA<br/>static files"]
        proxy["Reverse proxy<br/>/api/ → API"]
    end
    subgraph api["API container (Spring Boot 4, Java 25)"]
        rest["REST API /api/v1<br/>+ SSE /notifications/stream"]
        jobs["Scheduled jobs<br/>(payment reminders, token cleanup)<br/>+ Spring Batch (Excel import)"]
    end
    db[("PostgreSQL 16<br/>Liquibase schema")]
    files[("Document storage<br/>AES-256-GCM encrypted")]
    av["ClamAV :3310"]
    smtp["SMTP / Mailpit"]
    sms["SMS gateway"]
    bam["BAM gateway"]

    browser -- "HTTPS, same origin" --> spa
    browser -- "HTTPS /api/v1 (JWT in HttpOnly cookies)" --> proxy
    proxy --> rest
    rest --> db
    jobs --> db
    rest --> files
    rest -- "INSTREAM scan" --> av
    rest --> smtp
    rest --> sms
    rest --> bam
```

| Container | Code | Runs as |
|---|---|---|
| Portal | frontend repo, `deploy/nginx/default.conf.template` | `ghcr.io/<owner>/credit-bladi-portal` (nginx, unprivileged) |
| API | this repo | `ghcr.io/<owner>/credit-bladi-api` (JRE 25, unprivileged) |
| PostgreSQL, ClamAV, Mailpit | `docker-compose.yml` | Official images |

In development the SPA runs on `ng serve` (`:4200`) and calls the API directly on `:8080` (CORS allows
`http://localhost:4200`). In production the browser only sees the portal's origin.

## Level 3: Components

### Backend layers

The dependency rule is enforced by [`ArchitectureTest`](../src/test/java/com/credit/bladi/unittest/architecture/ArchitectureTest.java):
an arrow means "may depend on"; nothing points back up.

```mermaid
flowchart TB
    http(["HTTP request"])
    subgraph config["config"]
        filters["Security filter chain<br/>CachedBodyRequestFilter → RateLimitFilter → JwtAuthFilter<br/>+ RequestContext / RequestLogger interceptors"]
    end
    controller["<b>controller</b><br/>REST endpoints, DTOs (records + Bean Validation),<br/>DTO ↔ model mappers"]
    service["<b>service</b><br/>use cases, transactions, authorization rules,<br/>audit, notifications, declares the ports"]
    repository["<b>repository</b><br/>ports I*Repository and their adapters"]
    datasource["<b>datasource</b><br/>Spring Data JPA interfaces, entities, entity mappers"]
    infrastructure["<b>infrastructure</b><br/>adapters for the service ports: storage, ClamAV,<br/>SMTP, SMS, BAM, SSE"]
    model["<b>model</b><br/>domain: aggregates (Demand), value objects (Money, Rate, Rib),<br/>policies, events: no framework, no persistence"]
    db[("PostgreSQL")]
    ext["ClamAV · SMTP · SMS · BAM · disk"]

    http --> filters --> controller --> service
    service --> repository --> datasource --> db
    service -. "port interfaces" .-> infrastructure --> ext
    controller --> model
    service --> model
    repository --> model
```

### Frontend layers

```mermaid
flowchart TB
    user(["User action<br/>(click, form submit, navigation)"])
    routes["app.routes.ts<br/>lazy routes + guards (auth, guest, manager, client)"]
    pages["<b>pages/</b><br/>standalone components (OnPush, signals):<br/>simulators, admin back office, client portal"]
    shared["<b>shared/</b><br/>reusable components, validators"]
    services["<b>core/services/</b><br/>one service per API resource (HttpClient)"]
    interceptors["<b>core/interceptors/</b><br/>auth (withCredentials) → language (Accept-Language)<br/>→ error (401 → refresh once, else /login)"]
    models["<b>core/models/</b><br/>TypeScript contracts of the API DTOs"]
    i18n["public/i18n/*/fr|en|ar.json<br/>ngx-translate"]
    api(["API /api/v1"])

    user --> routes --> pages
    pages --> shared
    pages --> services --> interceptors --> api
    pages --> i18n
    services --> models
    pages --> models
```

### Generated dependency graph

The package-level dependency graph is generated from the source code, so it never goes stale:
[dependency-graph.md](dependency-graph.md) (regenerate with `python3 scripts/docs/dependency_graph.py`).
