[Back to README](../README.md) · [Glossary](glossary.md)

# End-to-End Journeys

Every API endpoint placed in a complete journey, from the public simulation to the last repayment, with each
success, refusal and error and its HTTP status. This is the business view; the path of each call through the
classes is in [Feature Flows](flows/README.md). The rules are detailed in [Mortgage Credit](mortgage-credit-workflow.md)
and [Enterprise Credit](enterprise-credit-workflow.md), the endpoint list in [API](api.md).

## Contents

1. [Overview](#1-overview)
2. [Mortgage simulation → dossier](#2-mortgage-simulation--dossier)
3. [Investment credit simulation → dossier](#3-investment-credit-simulation--dossier)
4. [Workflows and their steps](#4-workflows-and-their-steps)
5. [Classifying a dossier](#5-classifying-a-dossier)
6. [Advancing a dossier (`/advance`)](#6-advancing-a-dossier-advance)
7. [Assignment and reassignment](#7-assignment-and-reassignment)
8. [Dossier documents and document requests](#8-dossier-documents-and-document-requests)
9. [After the decision: credit file, payments, late interest](#9-after-the-decision-credit-file-payments-late-interest)
10. [Early repayment](#10-early-repayment)
11. [Client portal: account, invitation, enterprise access](#11-client-portal-account-invitation-enterprise-access)
12. [Regulatory parameters (managers)](#12-regulatory-parameters-managers)
13. [Bulk Excel import](#13-bulk-excel-import)
14. [Cross-cutting: audit, notifications, scheduled jobs](#14-cross-cutting-audit-notifications-scheduled-jobs)
15. [Scenario catalogue (live end-to-end run)](#15-scenario-catalogue-live-end-to-end-run)

---

## 1. Overview

The two public simulations are the **entry points**: an eligible simulation creates a dossier (`Demand`) in
the **same workflow engine**. The dossier follows its workflow up to the committee decision; if granted and
carried through to the last step, a **credit file** (repayment schedule) opens automatically.

```mermaid
flowchart LR
    subgraph Public
        SIM_M["POST /mortgage-credit<br/>mortgage simulation"]
        SIM_I["POST /investment-credit<br/>investment simulation"]
    end
    subgraph Back-office Manager
        IMP["POST /demands/import<br/>Excel import"]
        WF["Workflow<br/>classify → advance → … → decision"]
        CF["Credit file<br/>installments, payments, early repayment"]
        PAR["Parameters<br/>rate grids, BAM, Mourabaha"]
    end
    subgraph Client portal
        CLI["Portfolio, documents,<br/>payments, early repayment quote"]
    end

    SIM_M -- "eligible" --> WF
    SIM_I -- "eligible" --> WF
    IMP -- "valid row" --> WF
    PAR -. "rules and rates" .-> SIM_M
    PAR -. "rules and rates" .-> SIM_I
    PAR -. "same rules" .-> WF
    WF -- "last step passed<br/>(event after commit)" --> CF
    WF <-- "document requests" --> CLI
    CF <-- "payment declaration /<br/>validation" --> CLI
```

**Key consistency rule**: the simulation and the workflow apply **exactly the same business rules** (same
code). A manager who changes the amount, rate or duration during instruction can never obtain a dossier a
simulation would have refused.

---

## 2. Mortgage simulation → dossier

`POST /api/v1/mortgage-credit`: public, rate-limited (429 over quota).

```mermaid
flowchart TD
    A(["POST /api/v1/mortgage-credit"]) --> V{"Valid fields?<br/>(amount, duration 12–300 months,<br/>CSP, email, phone…)"}
    V -- no --> E400["❌ 400 validation"]
    V -- yes --> RL{"Request quota<br/>exceeded?"}
    RL -- yes --> E429["❌ 429"]
    RL -- no --> BAM{"Active BAM<br/>parameters?"}
    BAM -- no --> E503["❌ 503 BAM_PARAMETERS_NOT_FOUND"]
    BAM -- yes --> P{"productType?"}

    P -- "CONVENTIONAL (default)" --> C1{"Rate grid for<br/>the CSP found?"}
    C1 -- no --> E404["❌ 404 CSP_NOT_FOUND"]
    C1 -- yes --> C2{"Duration within the<br/>CSP profile's range?"}
    C2 -- no --> R1["❌ 422 DURATION_LIMIT_EXCEEDED"]
    C2 -- yes --> CM["Common rules"]

    P -- MOURABAHA --> M1{"Active Mourabaha<br/>parameters?"}
    M1 -- no --> E503b["❌ 503 MOURABAHA_PARAMETERS_NOT_FOUND"]
    M1 -- yes --> CM

    CM --> L{"Enough deposit?<br/>(LTV)"}
    L -- no --> R2["❌ 422 DEPOSIT_INSUFFICIENT"]
    L -- yes --> AG{"Age at maturity<br/>within the limit?"}
    AG -- no --> R3["❌ 422 AGE_LIMIT_EXCEEDED"]
    AG -- yes --> DR{"Debt ratio<br/>(installment + insurance) ≤ cap?"}
    DR -- no --> R4["❌ 422 DEBT_RATIO_EXCEEDED"]
    DR -- yes --> OK["✅ Offer computed<br/>rate · installment · TAEG · fees · schedule"]

    R1 & R2 & R3 & R4 --> AUD_R[("Audit SIMULATION_REJECTED<br/>no dossier created")]
    OK --> DUP{"Same person already has an<br/>unclassified mortgage dossier?"}
    DUP -- yes --> REF["Same dossier refreshed<br/>with the new terms<br/>audit DEMAND_REFRESHED_BY_SIMULATION"]
    REF --> RESP
    DUP -- no --> DEM["Dossier created<br/>workflow MORTGAGE_RETAIL<br/>step CLASSIFICATION_STEP<br/>agreed rate frozen on the dossier"]
    DEM --> NOT["Chargés d'affaires notified<br/>(after commit)"]
    DEM --> AUD[("Audit SIMULATION<br/>+ demandReference")]
    DEM --> RESP["200: response with demandReference"]
```

| Case | Result |
|---|---|
| Conventional, SALA profile, 240 months, enough deposit and income | 200 + dossier created |
| Conventional, SALA, 300 months (SALA range: 12–240) | 422 `DURATION_LIMIT_EXCEEDED`, no dossier |
| Deposit too low | 422 `DEPOSIT_INSUFFICIENT` |
| Too old at the end of the loan | 422 `AGE_LIMIT_EXCEEDED` |
| Installment too heavy for the income (co-borrower included) | 422 `DEBT_RATIO_EXCEEDED` |
| Eligible Mourabaha | 200 + dossier; the **agreed margin** is frozen on the dossier |
| Same person (same email + name) simulates again before anyone classified their dossier | 200 + **same** `demandReference`; the dossier's terms are replaced by the new simulation (audited) |
| Same person simulates again after classification | 200 + a new dossier |
| Missing grid on the bank side | 503 (configuration error, not the client's) |

> **Mourabaha**: no per-CSP duration range (the product doesn't have one in simulation either), TTC installment
> (margin + VAT); the agreed margin stays the one used by the final schedule even if the bank later changes its
> margin.

---

## 3. Investment credit simulation → dossier

`POST /api/v1/investment-credit`: public, rate-limited.

```mermaid
flowchart TD
    A(["POST /api/v1/investment-credit"]) --> V{"Valid fields?"}
    V -- no --> E400["❌ 400 validation"]
    V -- yes --> RL{"Quota exceeded?"}
    RL -- yes --> E429["❌ 429"]
    RL -- no --> P{"Active parameters for<br/>the enterprise type?"}
    P -- no --> E503["❌ 503 PARAMETERS_NOT_FOUND"]
    P -- yes --> A1{"Company old<br/>enough?"}
    A1 -- no --> R1["❌ 422 COMPANY_AGE_INSUFFICIENT"]
    A1 -- yes --> A2{"Amount / revenue<br/>≤ financing ratio?"}
    A2 -- no --> R2["❌ 422 FINANCING_RATIO_EXCEEDED"]
    A2 -- yes --> A3{"Guarantees cover<br/>the amount?"}
    A3 -- no --> R3["❌ 422 GUARANTEE_COVERAGE_INSUFFICIENT"]
    A3 -- yes --> RATE["Rate = grid formula<br/>(min/max/average)"]
    RATE --> A4{"Total debt service<br/>/ net result ≤ cap?"}
    A4 -- no --> R4["❌ 422 DEBT_SERVICE_RATIO_EXCEEDED"]
    A4 -- yes --> OK["✅ Offer: rate · installment · TAEG"]

    R1 & R2 & R3 & R4 --> AUD_R[("Audit ENTERPRISE_SIMULATION_REJECTED")]
    OK --> WFC{"Enterprise type?"}
    WFC -- "TPE · AUTO_ENTREPRENEUR · PME" --> W1["Dossier → workflow PME_INVESTMENT"]
    WFC -- "ETI · GE" --> W2["Dossier → workflow GE_INVESTMENT"]
    W1 & W2 --> DUP{"Same company + email already has an<br/>unclassified dossier on this workflow?"}
    DUP -- yes --> REF["Same dossier refreshed<br/>(same reference, audited)"]
    REF --> RESP
    DUP -- no --> DEM["Step CLASSIFICATION_STEP<br/>agreed rate frozen"]
    DEM --> AUD[("Audit ENTERPRISE_SIMULATION")]
    DEM --> RESP["200 + demandReference"]
```

---

## 4. Workflows and their steps

Steps are **data** (`workflow`, `process`, `step` tables), not code. Each step carries flags: required
grade/team, mandatory classification, insurance confirmation, **four-eyes**, client notification on entry.

### PME_INVESTMENT and GE_INVESTMENT (same workflow, different PPIF teams)

```mermaid
flowchart TD
    S0["CLASSIFICATION_STEP<br/>Chargé d'affaires · Réseau<br/>🔒 classification required"] --> S1["INITIATION_STEP<br/>Chargé d'affaires · Réseau"]
    S1 --> S2["INSTRUCTION_TS_STEP<br/>Chargé d'investissement · PPIF"]
    S2 --> S3["INSTRUCTION_MONTAGE_STEP<br/>Chargé d'investissement · PPIF"]
    S3 --> S4["INSTRUCTION_VALIDATION_STEP<br/>Directeur · PPIF"]
    S4 --> S5["RISK_RECEPTION_STEP<br/>Head of department · PAE"]
    S5 --> S6["RISK_ANALYSIS_STEP<br/>Analyst · PAE"]
    S6 --> S7["RISK_VALIDATION_DEPT_STEP<br/>Head of department · PAE"]
    S7 --> S8["RISK_VALIDATION_DIR_STEP<br/>Head of division · PAE"]
    S8 --> S9["RISK_COMMITTEE_PROG_STEP<br/>Admin support · PAE<br/>(committee type)"]
    S9 --> S10["DECISION_STEP<br/>Decision maker · PAE<br/>👁 four-eyes · 📩 client notified"]
    S10 --> S11["CONTRACT_EDITION_STEP<br/>Admin support · PAE"]
    S11 --> S12["CONTRACT_SIGNATURE_STEP<br/>Chargé d'affaires · Réseau"]
    S12 --> S13["INSURANCE_VALIDATION_STEP<br/>Gestionnaire · OGS Garanties<br/>🛡 ADI (+ damage if guarantees)"]
    S13 --> S14["GUARANTEES_NOTIF_STEP<br/>Gestionnaire · OGS Garanties"]
    S14 --> S15["GUARANTEES_MISE_EN_GESTION_STEP<br/>Gestionnaire · OGS MEG"]
    S15 --> END(["Dossier completed → credit file"])
```

### MORTGAGE_RETAIL (individual mortgage)

```mermaid
flowchart TD
    M0["CLASSIFICATION_STEP<br/>Chargé d'affaires<br/>🔒 classification required"] --> M1["MORTGAGE_INSTRUCTION_STEP<br/>Study and final computation"]
    M1 --> M2["MORTGAGE_DECISION_STEP<br/>Branch manager<br/>👁 four-eyes · 📩 client notified"]
    M2 --> M3["MORTGAGE_EDITION_STEP<br/>Loan offer edition"]
    M3 --> M4["MORTGAGE_GUARANTEE_STEP<br/>Mortgage / notary"]
    M4 --> M5["MORTGAGE_DISBURSEMENT_STEP<br/>Disbursement"]
    M5 --> END(["Dossier completed → credit file"])
```

Dossier lifecycle (`DemandStatus`, derived):

```mermaid
stateDiagram-v2
    [*] --> IN_PROGRESS: eligible simulation / import
    IN_PROGRESS --> IN_PROGRESS: advance · return · assign
    IN_PROGRESS --> REJECTED: classify FAKE / SIMULATION_ONLY
    IN_PROGRESS --> REJECTED: committee decision REJECTED
    IN_PROGRESS --> COMPLETED: last step passed
    COMPLETED --> [*]: credit file opened
    REJECTED --> [*]
```

---

## 5. Classifying a dossier

`POST /api/v1/demands/{id}/classify`: manager. The only way out of `CLASSIFICATION_STEP`.

```mermaid
flowchart TD
    A(["POST /demands/{id}/classify"]) --> S{"Dossier at a<br/>classification step?"}
    S -- no --> E1["❌ 409 NOT_AT_CLASSIFICATION_STEP"]
    S -- yes --> ELIG{"Actor eligible<br/>(assignee / grade + team)?"}
    ELIG -- no --> E2["❌ 403"]
    ELIG -- yes --> O{"classificationOutcome?"}
    O -- "FAKE · SIMULATION_ONLY" --> REJ["Dossier REJECTED<br/>audit DEMAND_CLASSIFIED"]
    O -- REAL --> T{"Enterprise credit?"}
    T -- yes --> EN{"enterpriseId given<br/>and existing?"}
    EN -- "not given" --> E3["❌ 422 ENTERPRISE_REQUIRED_FOR_REAL_CLASSIFICATION"]
    EN -- "unknown" --> E4["❌ 404 ENTERPRISE_NOT_FOUND"]
    EN -- yes --> AS
    T -- "no (mortgage)" --> AS{"Next assignee valid?<br/>(exists, staff, eligible)"}
    AS -- "unknown" --> E5["❌ 404"]
    AS -- "not eligible" --> E6["❌ 403"]
    AS -- yes --> GO["Next step opened<br/>client notified “dossier accepted”"]
    GO --> RIB{"clientRib given?<br/>(enterprise)"}
    RIB -- yes --> RIBV{"Valid Moroccan RIB?"}
    RIBV -- no --> E7["❌ 400"]
    RIBV -- yes --> RIBS["RIB saved<br/>audit ENTERPRISE_RIB_UPDATED (masked)"]
    GO --> CLI{"Client accounts with<br/>the contact's email?"}
    CLI -- "exactly 1" --> LINK["Enterprise: client link approved<br/>Mortgage: client becomes the borrower"]
    CLI -- "none" --> INV["Email invitation<br/>(see §11)"]
    CLI -- "several" --> AMB["Nothing automatic<br/>(ambiguous, logged)"]
```

The same three-way resolution runs for both products. For an investment dossier it grants access to the
**enterprise** (and so to all its dossiers); for a mortgage, which has no enterprise, it grants access to **that
dossier only** by recording its borrower (`mortgage_borrower`, audit `MORTGAGE_BORROWER_LINKED`).

---

## 6. Advancing a dossier (`/advance`)

`POST /api/v1/demands/{id}/advance`: manager. Closes the current step with a rate/amount/duration decision
(`KEEP` or `MANUAL`) and opens the next one (or the previous one when returning for completion).

```mermaid
flowchart TD
    A(["POST /demands/{id}/advance"]) --> C1{"Dossier still open?"}
    C1 -- no --> E1["❌ 409 NO_NEXT_STEP"]
    C1 -- yes --> C2{"Classification step?"}
    C2 -- yes --> E2["❌ 409 MUST_CLASSIFY_FIRST"]
    C2 -- no --> C3{"Actor = assignee,<br/>or grade + team OK?"}
    C3 -- no --> E3["❌ 403 ACTOR_NOT_ASSIGNED / NOT_ELIGIBLE"]
    C3 -- yes --> C4{"decisionOutcome = DEFERRED<br/>without a return?"}
    C4 -- yes --> E4["❌ 409 DECISION_DEFERRED_REQUIRES_RETURN"]
    C4 -- no --> C5{"Four-eyes step and the actor<br/>already closed a step of the dossier?"}
    C5 -- yes --> E5["❌ 403 FOUR_EYES_VIOLATION"]
    C5 -- no --> C6{"KEEP / MANUAL values<br/>consistent?"}
    C6 -- "KEEP with no existing rate" --> E6["❌ 409 RATE_OPTION_KEEP_UNAVAILABLE"]
    C6 -- "MANUAL without a value" --> E7["❌ 422 MANUAL_*_REQUIRED"]
    C6 -- ok --> C7{"Manual rate above the<br/>legal maximum (TMIC)?"}
    C7 -- yes --> E8["❌ 422 RATE_ABOVE_LEGAL_MAXIMUM"]
    C7 -- no --> C8{"decisionOutcome = REJECTED<br/>without a return?"}
    C8 -- yes --> REJ["Dossier REJECTED<br/>client notified “refused”<br/>audit DEMAND_REJECTED<br/>never reaches contract or disbursement"]
    C8 -- no --> C9{"Terms changed<br/>(MANUAL)?"}
    C9 -- yes --> C10{"Same rules as the<br/>product's simulation OK?"}
    C10 -- no --> E9["❌ 422 (DURATION_LIMIT, LTV, age,<br/>debt ratio, financing…)"]
    C10 -- yes --> C11
    C9 -- no --> C11{"Insurance step<br/>(no return)?"}
    C11 -- "yes, ADI / damage missing" --> E10["❌ 422 INSURANCE_*_REQUIRED"]
    C11 -- ok --> D{"returnReasonCode given?"}
    D -- "yes (return)" --> PREV{"Previous step?"}
    PREV -- no --> E11["❌ 409 NO_PREVIOUS_STEP"]
    PREV -- yes --> OPEN
    D -- no --> NEXT{"Next step?"}
    NEXT -- yes --> OPEN["Valid assignee?<br/>(required if the step has grade + team)"]
    OPEN -- no --> E12["❌ 422 NEXT_ASSIGNEE_REQUIRED<br/>/ 403 / 404"]
    OPEN -- yes --> MOVE["Step closed (who, what, before → after)<br/>next step opened + assignee notified<br/>client notified if the step asks for it"]
    NEXT -- "no (last)" --> DONE["Dossier COMPLETED<br/>client notified “finalized”<br/>event → credit file (§9)"]
```

Every advance is audited with the exact changes, e.g. `changes={rate: 5.25 -> 5.75; duration: 240 -> 180}`,
targeted at the dossier (see §14).

---

## 7. Assignment and reassignment

```mermaid
sequenceDiagram
    actor M as Manager
    participant API as /demands/{id}
    participant P as StepAssignmentPolicy
    participant N as Notifications

    M->>API: GET /eligible-assignees?direction=CURRENT|NEXT|PREVIOUS
    API->>P: who may take the step?
    P-->>M: team supervisor → whole team · otherwise → same grade + team
    M->>API: POST /assign {username}
    API->>P: dossier open? assigner allowed? target eligible?
    alt step without grade/team (mortgage)
        P-->>API: any existing staff account accepted
    else refused
        P-->>M: 409 closed dossier · 403 not eligible · 404 unknown
    end
    API->>N: notify the new assignee (after commit)
    API-->>M: 200 current step · assignment history + audit DEMAND_ASSIGNED
```

---

## 8. Dossier documents and document requests

```mermaid
sequenceDiagram
    actor M as Manager
    actor C as Client
    participant D as DocumentService
    participant S as Encrypted storage (AES-256)

    M->>D: POST /demands/{id}/documents (file, type)
    D->>D: size · real type sniffed (not the extension) · antivirus · SHA-256
    alt file refused
        D-->>M: 422 FILE_TOO_LARGE / FILE_TYPE_NOT_ALLOWED / FILE_INFECTED
    else accepted
        D->>S: encrypted write (deleted if the transaction fails)
        D-->>M: 200 (the previous version of the same type is superseded)
    end

    M->>D: POST /demands/{id}/document-requests {documentType}
    D-->>C: “document requested” notification (after commit)
    C->>D: GET /me/demands/{id}/document-requests
    C->>D: POST /me/demands/{id}/document-requests/{rid}/documents
    D->>S: same checks + encrypted storage
    D-->>M: “document received” notification
    M->>D: GET /demands/{id}/documents/{docId}/download
    D->>S: read + decrypt
    D-->>M: original file (409 DOCUMENT_NOT_READY if the scan isn't clean)
```

---

## 9. After the decision: credit file, payments, late interest

Once the last step is passed, an event opens the credit file **after commit**: schedule computed at the
**rate / margin agreed** on the dossier, first installment at disbursement + 30 days (`Africa/Casablanca`
calendar).

```mermaid
stateDiagram-v2
    [*] --> PENDING: credit file opened
    PENDING --> LATE: due date passed (08:00 job)
    PENDING --> DECLARED: client declares having paid (source RIB)
    LATE --> DECLARED: client declares
    DECLARED --> PAID: manager validates (late interest charged)
    DECLARED --> PENDING: manager rejects (not yet due)
    DECLARED --> LATE: manager rejects (already due)
    PENDING --> REPAID_EARLY: total early repayment
    PAID --> [*]
    REPAID_EARLY --> [*]
```

```mermaid
sequenceDiagram
    actor C as Client
    actor M as Dossier manager
    participant CF as CreditFileService

    C->>CF: GET /me/demands/{id}/credit-file
    CF-->>C: schedule + accrued late interest + total due + beneficiary RIB
    C->>CF: POST /me/…/installments/{iid}/payments {sourceRib}
    alt invalid RIB · already paid · already declared · file closed
        CF-->>C: 400 / 409 INSTALLMENT_ALREADY_PAID / _DECLARED / CREDIT_FILE_CLOSED
    else ok
        CF-->>C: 200 DECLARED
        CF-->>M: “payment declared” notification (after commit)
    end
    alt payment found on the statement
        M->>CF: POST /demands/{id}/credit-file/installments/{iid}/payments/validate
        CF->>CF: PAID + late interest = installment × (rate + 2 pts) × days / 360<br/>(none for Mourabaha)
        CF-->>C: “payment received” notification
        CF->>CF: last installment settled? → file CLOSED
    else not found
        M->>CF: POST …/payments/reject {reason}
        CF-->>C: “payment not validated” notification + reason
    end
```

---

## 10. Early repayment

Endpoints: manager `GET …/credit-file/early-repayments/quote?amount=`, `POST …/early-repayments`,
`GET …/early-repayments`; client `GET /me/…/credit-file/early-repayments/quote`.

```mermaid
flowchart TD
    A(["Quote or record"]) --> F{"Credit file<br/>active?"}
    F -- no --> E1["❌ 409 CREDIT_FILE_CLOSED"]
    F -- yes --> MU{"Mourabaha?"}
    MU -- yes --> E2["❌ 422 EARLY_REPAYMENT_NOT_AVAILABLE_FOR_PRODUCT"]
    MU -- no --> AR{"Installment late<br/>or awaiting validation?"}
    AR -- yes --> E3["❌ 409 EARLY_REPAYMENT_ARREARS_OUTSTANDING"]
    AR -- no --> CRD["Outstanding capital =<br/>present value of the remaining installments"]
    CRD --> AM{"amount?"}
    AM -- "≤ 0 or > outstanding capital" --> E4["❌ 422 EARLY_REPAYMENT_AMOUNT_INVALID"]
    AM -- "absent or = outstanding capital" --> TOT["TOTAL"]
    AM -- "< outstanding capital" --> PAR["PARTIAL"]
    TOT & PAR --> IND["Indemnity (loi 31-08) =<br/>min(1 month of interest on the repaid capital,<br/>3% of the outstanding capital)<br/>+ accrued interest"]
    IND --> Q{"Quote or record?"}
    Q -- "GET /quote" --> QR["200 quote (nothing changed)"]
    Q -- "POST (manager)" --> X{"Total?"}
    X -- yes --> XT["Remaining installments → REPAID_EARLY<br/>file CLOSED<br/>client notified “credit repaid”"]
    X -- no --> XP["Same number of installments,<br/>lower monthly amount<br/>client notified with the new amount"]
    XT & XP --> AUD[("early_repayment history<br/>+ audit EARLY_REPAYMENT (mandatory)")]
```

---

## 11. Client portal: account, invitation, enterprise access

```mermaid
flowchart TD
    subgraph Account
        R["POST /auth/register"] --> LG["POST /auth/login<br/>access + refresh cookies"]
        LG --> RF["POST /auth/refresh<br/>rotation · reuse detected → all revoked"]
        LG --> LO["POST /auth/logout<br/>tokens revoked, audit LOGOUT"]
    end

    subgraph "Access to an enterprise (3 paths)"
        A1["Classification REAL<br/>1 client with the contact's email"] --> OK1["Link APPROVED automatically"]
        A2["Classification REAL<br/>no client with that email"] --> INV["Invitation email"]
        INV --> PV["GET /invitations/{token}<br/>preview (expired → refused)"]
        PV --> AC["POST /invitations/{token}/accept<br/>creates the CLIENT account + APPROVED link"]
        A3["Client: POST /me/enterprise-links<br/>(claims via the RC number)"] --> PEN["Link PENDING"]
        PEN --> MGR{"Manager"}
        MGR -- "PATCH approve" --> OK2["APPROVED"]
        MGR -- "PATCH reject" --> KO["REJECTED"]
        A4["Manager: POST /enterprises/{id}/user-links<br/>(existing client)"] --> OK3["APPROVED directly"]
    end

    subgraph "Access to a mortgage dossier (borrower)"
        B1["Classification REAL<br/>1 client with the applicant's email"] --> BOK["Client becomes the borrower"]
        B2["Classification REAL<br/>no client with that email"] --> BINV["Invitation email naming the dossier"]
        BINV --> BAC["POST /invitations/{token}/accept<br/>creates the CLIENT account + borrower link"]
    end

    OK1 & AC & OK2 & OK3 --> PORT["GET /me/portfolio<br/>GET /me/portfolio/{enterpriseId}/demands"]
    BOK & BAC --> MPORT["GET /me/mortgage-demands"]
    PORT & MPORT --> USE["Requested documents · credit file ·<br/>declare a payment · early repayment quote"]
    OK2 -. "PATCH revoke" .-> REV["Access removed"]
```

Every `/me/**` endpoint checks the caller's right to **that** dossier: an **APPROVED** link to its enterprise
for an investment dossier, being its **borrower** for a mortgage (`ClientDemandAccessGuard`). An id guessed in
the URL never gives access to another client's dossier. A mortgage invitation preview carries the dossier
reference (`demandReference`) instead of an enterprise name.

---

## 12. Regulatory parameters (managers)

```mermaid
flowchart LR
    CR["/credit-rates<br/>grid per CSP:<br/>min/max rate, durations"] --> MS["Conventional mortgage<br/>simulation"]
    BAM["/bam-parameters<br/>LTV, debt ratio, age,<br/>fees, ADI insurance"] --> MS
    BAM --> MM["Mourabaha simulation"]
    MOU["/mourabaha-parameters<br/>margin, VAT, Takaful"] --> MM
    INV["/investment-credit-parameters<br/>per enterprise type"] --> IS["Investment simulation"]
    MS & MM & IS -. "same rules" .-> WF["Workflow: re-check<br/>of changed terms"]
    CFG["Configuration:<br/>TMIC 14%, late margin +2 pts,<br/>indemnity 1 month / 3%"] --> WF
    CFG -. "rateMax ≤ TMIC<br/>(else 422)" .-> CR
    CFG -.-> INV
    CFG --> SV["Credit file"]
```

Each `PATCH` creates a new active version (a `/credit-rates` or `/investment-credit-parameters` grid whose `rateMax` would exceed the TMIC is refused with `422 RATE_ABOVE_LEGAL_MAXIMUM` and nothing is saved); the previous one stays for history. The change is audited field by
field (**mandatory** audit: if the audit write fails, the change is rolled back). An existing dossier keeps
**its** agreed rate or margin: changing a grid only affects new simulations.

---

## 13. Bulk Excel import

```mermaid
sequenceDiagram
    actor M as Manager
    participant I as DemandImportService
    participant B as Spring Batch job
    participant W as Workflow engine

    M->>I: POST /demands/import (.xlsx file)
    I->>I: not empty · size · real readable Excel · no import running
    alt refused
        I-->>M: 422 FILE_EMPTY / FILE_TOO_LARGE / FILE_TYPE_NOT_ALLOWED · 409 JOB_ALREADY_RUNNING
    else accepted
        I-->>M: 202 {jobExecutionId}
        I->>B: asynchronous launch
        loop each row
            B->>B: read + validate
            alt valid row
                B->>W: createDemand (same engine as a simulation)
            else invalid row
                B->>B: error recorded, row skipped
            end
        end
        B->>B: file deleted when the job ends (personal data)
    end
    M->>I: GET /demands/import/{jobExecutionId}
    I-->>M: status · read · created · skipped · per-row errors
```

---

## 14. Cross-cutting: audit, notifications, scheduled jobs

```mermaid
flowchart LR
    ACT["Any business action"] --> AUD[("Audit<br/>who · what · when · IP<br/>target (dossier/enterprise/user)<br/>changes before → after")]
    AUD --> Q["GET /audit-log?entityType=DEMAND&entityId=…<br/>full trail of one dossier"]
    ACT --> AC["After commit only"]
    AC --> CH1["In-app + real time (SSE)<br/>GET /notifications, /stream"]
    AC --> CH2["Email"]
    AC --> CH3["SMS (clients)"]
    T1["Every day 08:00"] --> REM["Overdue installments → LATE<br/>+ reminder (max 1 / 3 days)"]
    T2["Every day 03:00"] --> PUR["Purge of expired tokens and invitations"]
```

- **Mandatory audit** (credit decisions, payments, parameters, roles, RIBs, early repayments): if the audit
  write fails, the operation is rolled back.
- **Notifications**: never sent for an operation that was finally rolled back; a delivery failure never undoes
  the operation; emails/phones are masked in logs; each one is written in the recipient's language (fr/en/ar,
  from `Accept-Language` at simulation, registration or invitation acceptance), French when unknown.

---

## 15. Scenario catalogue (live end-to-end run)

Every journey above, played end to end against the real application (dev profile: PostgreSQL, ClamAV,
Mailpit, encrypted storage) by `scripts/e2e/run_e2e.py`: see [Testing → Live end-to-end scenario run](testing.md#live-end-to-end-scenarios).
Last run: **2026-10-21, 60 of 60 passed**. The same journeys are also played in the browser by the portal's
Playwright suite (130 tests, all passing), whose screenshots illustrate the portal's
[User Guide](https://github.com/LouziAmine/---Bladi-Credit-Portal---/blob/release/2.1.0/docs/user-guide/README.md).
Each scenario uses its own accounts and data, so the run can be repeated on the same database.


### Setup

| # | Scenario | Result |
|---|---|---|
| S-01 | Admin logs in and creates staff for every grade/team of the three workflows (x2 for four-eyes) | ✅ |

### A. Mortgage simulation

| # | Scenario | Result |
|---|---|---|
| A-01 | Conventional mortgage, eligible -> 200, dossier created at CLASSIFICATION_STEP | ✅ |
| A-02 | Same person simulates again before classification -> same dossier refreshed | ✅ |
| A-03 | Duration outside the CSP range (SALA 300 months) -> 422 DURATION_LIMIT_EXCEEDED, no dossier | ✅ |
| A-04 | Deposit insufficient (loan > LTV) -> 422 | ✅ |
| A-05 | Too old at maturity -> 422 | ✅ |
| A-06 | Debt ratio exceeded -> 422 | ✅ |
| A-07 | Mourabaha eligible -> 200 with TTC schedule | ✅ |
| A-08 | Validation: missing fields / bad nationality shape / unknown ISO country -> 400 with precise message | ✅ |
| A-09 | Error messages follow Accept-Language (fr/ar) | ✅ |

### B. Investment simulation

| # | Scenario | Result |
|---|---|---|
| B-01 | PME eligible -> 200, dossier on PME_INVESTMENT workflow | ✅ |
| B-02 | GE eligible -> dossier on GE_INVESTMENT workflow (PPIF_GE teams) | ✅ |
| B-03 | Company too young -> 422 | ✅ |
| B-04 | Amount / revenue above financing ratio -> 422 | ✅ |
| B-05 | Guarantees below required coverage -> 422 | ✅ |
| B-06 | Debt service ratio exceeded -> 422 | ✅ |
| B-07 | Same company + email simulates again before classification -> same reference | ✅ |

### C. Accounts & security

| # | Scenario | Result |
|---|---|---|
| C-01 | Client registers, logs in, /me, refresh rotation, logout revokes access | ✅ |
| C-02 | Duplicate username -> 409; weak password -> 400; wrong password -> 401 generic | ✅ |
| C-03 | Role separation: anonymous -> 401 on back-office, client -> 403 on back-office | ✅ |

### D. Classification

| # | Scenario | Result |
|---|---|---|
| D-01 | Advance while at CLASSIFICATION_STEP -> 409 MUST_CLASSIFY_FIRST | ✅ |
| D-02 | Classify REAL enterprise dossier without enterpriseId -> 422; unknown enterprise -> 404 | ✅ |
| D-03 | Actor without the step's grade/team cannot classify -> 403 | ✅ |
| D-04 | Classify FAKE -> dossier REJECTED, further advance -> 409 | ✅ |
| D-05 | Classify REAL with an invalid RIB -> 400, nothing changed | ✅ |
| D-06 | Classify REAL with enterprise + RIB + next assignee -> INITIATION_STEP, invitation mailed to the contact (no client account with that email) | ✅ |

### G. Client access

| # | Scenario | Result |
|---|---|---|
| G-01 | Invitation preview (public) then accept -> CLIENT account + APPROVED link, portfolio visible | ✅ |
| G-02 | Invitation token cannot be reused -> 4xx | ✅ |
| G-03 | Client claims an enterprise by RC number -> PENDING; manager rejects; claims again; approves; revokes -> access removed | ✅ |
| G-04 | IDOR: a client with no link cannot read another enterprise's dossier, documents or credit file | ✅ |

### F. Documents

| # | Scenario | Result |
|---|---|---|
| F-01 | Upload a real PDF (sniffed, scanned CLEAN by ClamAV, encrypted) and download the same bytes | ✅ |
| F-02 | Upload a text file renamed .pdf -> 422 FILE_TYPE_NOT_ALLOWED (real type is sniffed) | ✅ |
| F-03 | Upload a .docx carrying the EICAR test virus -> 422 FILE_INFECTED | ✅ |
| F-04 | Re-upload the same document type -> previous version SUPERSEDED | ✅ |
| F-05 | Document request -> client sees it, uploads, manager downloads the fulfilled document | ✅ |

### E. Instruction pipeline (PME)

| # | Scenario | Result |
|---|---|---|
| E-01 | Comments, history, assignment history and pipeline statuses are consistent | ✅ |
| E-02 | Only the assignee (or same grade+team) may advance -> 403 otherwise | ✅ |
| E-03 | Next assignee not eligible for the next step -> 403; missing -> 422 | ✅ |
| E-04 | Reassign: eligible-assignees CURRENT, assign to ineligible -> 403, eligible -> 200 | ✅ |
| E-05 | Walk the PME pipeline to DECISION_STEP (a return for completion on the way) | ✅ |
| E-06 | Decision rules: DEFERRED without return -> 409, MANUAL rate above TMIC -> 422, MANUAL amount breaking the financing ratio -> 422 | ✅ |
| E-07 | Committee APPROVED with a MANUAL rate within limits -> CONTRACT_EDITION_STEP, change audited | ✅ |
| E-08 | Insurance step without ADI confirmation -> 422, with ADI -> next step | ✅ |
| E-09 | Last step passed -> dossier COMPLETED, credit file opened with the full schedule at the agreed rate | ✅ |

### H. Payments

| # | Scenario | Result |
|---|---|---|
| H-01 | Client sees the credit file; declares a payment with a bad RIB -> 400; valid -> DECLARED; twice -> 409 | ✅ |
| H-02 | Manager rejects the declaration (not yet due -> PENDING), client declares again, manager validates -> PAID | ✅ |
| H-03 | Validate an installment the client never declared -> 409 | ✅ |
| H-04 | Client is notified by email of the payment received / rejected (bell is back-office only) | ✅ |

### I. Early repayment

| # | Scenario | Result |
|---|---|---|
| I-01 | Early repayment quote (client) then PARTIAL by the manager -> lower installments | ✅ |
| I-02 | TOTAL early repayment -> remaining installments REPAID_EARLY, credit file CLOSED, declaring afterwards -> 409 | ✅ |

### J. Mortgage pipeline

| # | Scenario | Result |
|---|---|---|
| J-01 | Mortgage: classify REAL (no enterprise needed) then four-eyes blocks the instructor from deciding | ✅ |
| J-02 | Mortgage: second pair of eyes decides, dossier goes to disbursement, credit file opens | ✅ |
| J-03 | Mortgage borrower (no enterprise): invited by e-mail at classification, creates an account, sees only their dossier | ✅ |
| J-06 | Mortgage borrower declares an installment payment, the manager validates it -> PAID | ✅ |
| J-04 | Mortgage committee REJECTED -> dossier REJECTED, client notified by email, no credit file | ✅ |
| J-05 | Mourabaha: early repayment is refused (422) once its credit file exists | ✅ |

### K. Parameters & reference data

| # | Scenario | Result |
|---|---|---|
| K-01 | Credit-rate grid with rateMax above the TMIC (14%) -> 422, nothing saved | ✅ |
| K-02 | BAM parameters update -> new active version, audited field by field | ✅ |
| K-03 | Document suggestions: create (CSP scope), duplicate -> 409, delete | ✅ |
| K-04 | BAM verification mock answers for an enterprise identifier | ✅ |

### Good to know

- **In-app notifications are back-office only**: `/api/v1/notifications/**` is `MANAGER`-only and the bell is
  hidden for clients; clients are notified by e-mail and SMS. Their in-app rows are stored but never shown.
- **Four-eyes is off in the `dev` profile** (`workflow.four-eyes.enabled=false`); it is on everywhere else.
- **`GET /demands/{id}` leaves `currentProcessName` / `currentStepName` empty**: only the list fills them;
  the detail screen reads `GET /demands/{id}/current-step`.
- **`/advance` requires `rateOption`, `amountOption` and `durationOption`** (`KEEP` or `MANUAL`) on every call,
  including steps that do not change terms.
