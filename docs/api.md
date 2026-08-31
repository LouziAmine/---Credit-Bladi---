[Back to README](../README.md) · [Glossary](glossary.md)

# API

This table is the map: what exists and who may call it. Request and response schemas, field constraints and
examples are in [Swagger UI](testing.md#swagger-ui). How the endpoints chain into journeys is in
[End-to-End Journeys](end-to-end-flows.md), and the path of each call through the code in
[Feature Flows](flows/README.md).

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Create a client account |
| POST | `/api/v1/auth/login` | Public | Authenticate. Issues access + refresh JWTs as HttpOnly cookies |
| POST | `/api/v1/auth/refresh` | Public | Rotate the access token using the refresh token |
| POST | `/api/v1/auth/logout` | Public | Revoke the current access and refresh tokens and clear the cookies |
| GET | `/api/v1/auth/me` | Authenticated | Return the current user's username and role |
| POST | `/api/v1/mortgage-credit` | Public | Mortgage simulation, conventional or Mourabaha (`productType`). If eligible, opens a dossier on the `MORTGAGE_RETAIL` workflow (`demandReference` in the response); the same person (e-mail and name) with an unclassified dossier gets that dossier refreshed instead. A rate above the legal ceiling is refused (`422`). The `Accept-Language` (fr, en, ar) is kept for the client's notifications. See [Mortgage Credit](mortgage-credit-workflow.md#from-simulation-to-tracked-dossier) |
| GET | `/api/v1/bam-parameters`, `/api/v1/bam-parameters/{id}` | Manager | View BAM regulatory parameters: deposit, debt ratio, age, insurance, fee ceilings |
| PATCH | `/api/v1/bam-parameters/{id}` | Manager | Update BAM regulatory parameters |
| GET | `/api/v1/credit-rates`, `/api/v1/credit-rates/{id}` | Manager | View Conventional credit rates per CSP profile |
| PATCH | `/api/v1/credit-rates/{id}` | Manager | Update a Conventional credit rate; a `rateMax` above the legal maximum (TMIC) is refused (`422 RATE_ABOVE_LEGAL_MAXIMUM`) |
| GET | `/api/v1/mourabaha-parameters`, `/api/v1/mourabaha-parameters/{id}` | Manager | View Mourabaha profit-margin, VAT and Takaful contribution parameters |
| PATCH | `/api/v1/mourabaha-parameters/{id}` | Manager | Update Mourabaha profit-margin, VAT or Takaful contribution parameters |
| GET | `/api/v1/activity-sectors` | Public | List every activity sector, alphabetically. Populates the enterprise credit simulation form |
| POST | `/api/v1/activity-sectors` | Manager | Add a new activity sector |
| GET | `/api/v1/document-suggestions?enterpriseType=\|cspCode=` | Manager | Suggested document labels for a document request, by enterprise type or CSP |
| POST | `/api/v1/document-suggestions` | Manager | Add a suggested document label (enterprise type **or** CSP, not both) |
| DELETE | `/api/v1/document-suggestions/{id}` | Manager | Remove a suggested document label |
| POST | `/api/v1/investment-credit` | Public | Investment simulation for a TPE, auto-entrepreneur, PME, ETI or GE. If eligible, opens a dossier or refreshes the same company's unclassified one. A rate above the legal ceiling is refused (`422`). See [Enterprise Credit](enterprise-credit-workflow.md) |
| POST | `/api/v1/demands/import` | Manager | Bulk-create demands from an uploaded `.xlsx` file (async Spring Batch job). Returns a `jobExecutionId` immediately; only one import may run at a time, see [Bulk demand creation](enterprise-credit-workflow.md#bulk-demand-creation-importing-existing-dossiers) |
| GET | `/api/v1/demands/import/{jobExecutionId}` | Manager | Poll a demand import job's status, read/write/skip counts, and the per-row error list for skipped rows |
| GET | `/api/v1/grades` | Manager | List every grade (read-only lookup used to profile users and route workflow assignment) |
| GET | `/api/v1/teams` | Manager | List every team, same purpose as grades |
| GET | `/api/v1/investment-credit-parameters/{id}`, `/api/v1/investment-credit-parameters/type/{enterpriseType}` | Manager | View eligibility/pricing parameters per enterprise type, including `insurancePremiumRatio` (monthly, flat ADI/dommages premium ratio: see [Insurance: pricing vs. the workflow step](enterprise-credit-workflow.md#insurance-pricing-and-the-workflow-step-are-two-different-things)) |
| PATCH | `/api/v1/investment-credit-parameters/{id}` | Manager | Update eligibility/pricing parameters for an enterprise type; a `rateMax` above the legal maximum (TMIC) is refused (`422 RATE_ABOVE_LEGAL_MAXIMUM`) |
| GET | `/api/v1/enterprises`, `/api/v1/enterprises/{id}` | Manager | List / view onboarded client companies (an Enterprise is the portfolio a `REAL`-classified demand gets attached to) |
| POST | `/api/v1/enterprises` | Manager | Create an Enterprise (client company) |
| GET | `/api/v1/enterprises/match?name=` | Manager | Look up an Enterprise by free-text company name, matched against `normalized_name` (uppercased, accents/legal-form suffixes like SARL/SA/SAS stripped, see `EnterpriseNameNormalizer`). Always returns `200`; `matched=false` if nothing's found. Used when classifying a demand `REAL`, to check whether the client already has a portfolio before a duplicate one gets created |
| POST | `/api/v1/enterprises/{enterpriseId}/user-links` | Manager | Invite an already-registered `CLIENT` (by `username`) to that Enterprise's portfolio. The link is created `APPROVED` immediately, see [Enterprise client portal access](enterprise-credit-workflow.md#enterprise-client-portal-access) |
| GET | `/api/v1/enterprises/{enterpriseId}/user-links` | Manager | List every client access link for one Enterprise (paginated), each with the linked client's `username` |
| GET | `/api/v1/enterprise-user-links?status=PENDING` | Manager | Cross-enterprise review queue of `PENDING` client-claim requests (paginated) |
| PATCH | `/api/v1/enterprise-user-links/{id}/approve` | Manager | Approve a `PENDING` client-claim link |
| PATCH | `/api/v1/enterprise-user-links/{id}/reject` | Manager | Reject a `PENDING` client-claim link, with an optional free-text reason |
| PATCH | `/api/v1/enterprise-user-links/{id}/revoke` | Manager | Revoke an `APPROVED` link, e.g. when an employee leaves the company |
| POST | `/api/v1/me/enterprise-links` | Client | Self-service claim of portal access to an Enterprise by its exact `registrationNumber` (RC/ICE). Lands `PENDING` until a Manager reviews it |
| GET | `/api/v1/me/enterprise-links` | Client | List the caller's own enterprise-link requests, every status |
| GET | `/api/v1/me/portfolio` | Client | List the Enterprises the caller has `APPROVED` portfolio access to |
| GET | `/api/v1/me/mortgage-demands` | Client | The caller's own mortgage dossiers (`ClientDemandSummaryResponseDTO`, most recently linked first). A mortgage borrower has no Enterprise: access is per dossier, granted at classification (single client account with the applicant's e-mail) or by accepting the e-mailed invitation |
| GET | `/api/v1/me/portfolio/{enterpriseId}/demands` | Client | Paginated, trimmed (`ClientDemandSummaryResponseDTO`) demand list for one Enterprise. `403` unless the caller holds an `APPROVED` link to it |
| GET | `/api/v1/invitations/{token}` | Public | Preview a pending client invitation (email, and either the target Enterprise name or, for a mortgage borrower, the dossier reference `demandReference`) before the invitee sets a password, see [Enterprise client portal access](enterprise-credit-workflow.md#enterprise-client-portal-access) |
| POST | `/api/v1/invitations/{token}/accept` | Public | Accept a client invitation. Creates the `CLIENT` account with a self-chosen password and, in the same step, its `APPROVED` enterprise link or, for a mortgage invitation, its borrower link to the dossier |
| GET | `/api/v1/demands`, `/api/v1/demands/{id}` | Manager | List / view credit demands (enterprise or mortgage, see `demandType` on the response) and their current pipeline position |
| GET | `/api/v1/demands/{id}/current-step`, `/api/v1/demands/{id}/history` | Manager | View a demand's current step, or its full step-by-step history |
| GET | `/api/v1/demands/{id}/pipeline` | Manager | Every step of the demand's workflow, in order, each flagged `DONE`/`CURRENT`/`UPCOMING`. Drives the progress view |
| POST | `/api/v1/demands/{id}/classify` | Manager | The chargé d'affaires' initial validation of a freshly-created demand, still at `CLASSIFICATION_STEP`: `REAL` opens the next step; for an **enterprise** demand this also requires `enterpriseId` and attaches it to that Enterprise's portfolio (see [Classification](enterprise-credit-workflow.md#classification-the-gate-before-the-pipeline)). A **mortgage** demand skips the Enterprise-linking entirely (see [Mortgage Credit Workflow](mortgage-credit-workflow.md#why-a-dossier-at-all-an-individual-isnt-a-company)). `FAKE`/`SIMULATION_ONLY` reject either type |
| POST | `/api/v1/mock/verification-bam` | Manager | Run a **simulated** SCIP (incidents de paiement chèques) + Centrale des Risques check ahead of classifying a demand. Every response is mock data (`source=MOCK_SIMULATION`), optionally forced to a specific `scenario` for a demo: see [BAM verification](enterprise-credit-workflow.md#bam-verification-a-mock-scip--centrale-des-risques-check) |
| POST | `/api/v1/demands/{id}/advance` | Manager | Closes the current step and opens the next one, or sends the dossier back one step when `returnReasonCode` is set. Rules below the table |
| GET | `/api/v1/demands/{id}/amortization` | Manager | Amortization schedule (tableau d'amortissement) at the demand's current amount/duration/rate, or an explicit `rate`/`amount`/`duration` preview (e.g. to replay a past step's own applied values). Dispatches by `demandType`/`productType`: enterprise and mortgage-Conventional are interest-based (`insurancePremium`/`allInMonthlyPayment`/`totalInsuranceCost`, ADI insurance); mortgage-Mourabaha is margin-based (`marginHt`/`vatOnMargin`/`marginTtc`/`monthlyPaymentTtc`) with its own Takaful contribution reported through those same generic insurance fields, at the margin agreed on the dossier (or an explicit `rate` override), see [Mortgage Credit Workflow](mortgage-credit-workflow.md#recomputing-the-amortization-schedule-later) |
| GET | `/api/v1/demands/{id}/eligible-assignees?direction=CURRENT\|NEXT\|PREVIOUS` | Manager | List users who may be assigned the targeted step (default: the demand's current step). Grade+team matches, or the required team's full roster if the caller is that team's Superviseur. Use `NEXT`/`PREVIOUS` to populate `assigneeUsername`'s dropdown ahead of `POST /advance` A step with no required grade/team (e.g. the `MORTGAGE_RETAIL` steps) lists every staff account. |
| POST | `/api/v1/demands/{id}/assign` | Manager | Assign/reassign the demand's current step to a user. A Superviseur of the required team may assign to any team member regardless of grade; a step with no grade/team requirement accepts any staff account (`404` for an unknown user). Triggers a `DEMAND_ASSIGNED` notification (in-app + real-time + email) to the new assignee |
| GET | `/api/v1/demands/{id}/assignment-history` | Manager | Full, append-only (re)assignment audit trail for the demand: every step, every previous/new assignee, who performed it and when: unlike the current-step card, which only shows the latest assignee. See [Assignment history](enterprise-credit-workflow.md#assignment-history-who-affected-what-and-when) |
| GET | `/api/v1/demands/{id}/comments` | Manager | List remarks left on the demand, oldest first |
| POST | `/api/v1/demands/{id}/comments` | Manager | Leave a remark on the demand's current step, without advancing, returning or (re)assigning it |
| POST | `/api/v1/demands/{id}/documents` | Manager | Upload a document on the demand's current step (multipart `file` + optional `documentType`). Content type is detected from the file's bytes and antivirus-scanned when scanning is enabled. Re-uploading the same `documentType` supersedes the previous ACTIVE document rather than replacing it |
| GET | `/api/v1/demands/{id}/documents` | Manager | List every document uploaded on the demand (ACTIVE and SUPERSEDED), oldest first |
| GET | `/api/v1/demands/{id}/documents/{documentId}/download` | Manager | Download a document. Blocked unless its antivirus scan verdict is `CLEAN` or `SKIPPED` |
| POST | `/api/v1/demands/{id}/document-requests` | Manager | Tag the demand with the name of a document the client still needs to provide (free-text `documentType`). Creates a `PENDING` request and notifies the client (email + SMS + in-app if linked): see [Document requests](enterprise-credit-workflow.md#document-requests-asking-the-client-for-a-specific-document) |
| GET | `/api/v1/demands/{id}/document-requests` | Manager | List every document request raised on the demand, with its status (`PENDING`/`FULFILLED`) |
| GET | `/api/v1/me/demands/{demandId}/document-requests` | Client | List the document requests tagged on one of the caller's own demands. `403` unless they hold an `APPROVED` link to that demand's Enterprise, or are the borrower of that mortgage dossier |
| POST | `/api/v1/me/demands/{demandId}/document-requests/{requestId}/documents` | Client | Fulfil a document request by uploading the file (multipart), reusing the Manager upload pipeline as-is (content-sniffing, size cap, antivirus scan). Marks the request `FULFILLED` and notifies the requesting Manager (email + in-app, no SMS) |
| GET | `/api/v1/me/demands/{demandId}/document-requests/{requestId}/documents/download` | Client | Download the document the caller uploaded to fulfil their own request |
| GET | `/api/v1/demands/{demandId}/credit-file` | Manager | View the Dossier de crédit opened once the demand completes its pipeline: disbursement, status (`ACTIVE`/`CLOSED`), beneficiary/client RIBs, and every installment with its live status, accrued late interest (`latePenalty`) and `totalDue`. See [Credit File Servicing](enterprise-credit-workflow.md#credit-file-servicing-after-the-demand-is-accorded) |
| POST | `/api/v1/demands/{demandId}/credit-file/installments/{installmentId}/payments/validate` | Manager | Confirm a client's declared payment actually landed on the bank statement. Marks the installment `PAID`, charges any late interest, notifies the client; closes the credit file once nothing is left to pay |
| POST | `/api/v1/demands/{demandId}/credit-file/installments/{installmentId}/payments/reject` | Manager | Reject a client's declared payment with a reason. Reverts the installment to `PENDING`/`LATE`, notifies the client |
| GET | `/api/v1/me/demands/{demandId}/credit-file` | Client | View the credit file for a demand in the caller's portfolio. `403` unless they hold an `APPROVED` link to its Enterprise, or are the borrower of that mortgage dossier |
| GET | `/api/v1/demands/{demandId}/credit-file/early-repayments/quote?amount=` | Manager | Price an early repayment made today (omit `amount` for a total one): outstanding capital, accrued interest, loi 31-08 capped indemnity, total, new monthly amount. See [Early repayment](enterprise-credit-workflow.md#early-repayment-remboursement-anticipé) |
| POST | `/api/v1/demands/{demandId}/credit-file/early-repayments` | Manager | Record an early repayment received by the bank, body `{amount}` (omit for total). Total: remaining installments `REPAID_EARLY`, credit file `CLOSED`; partial: lower monthly amount. Notifies the client |
| GET | `/api/v1/demands/{demandId}/credit-file/early-repayments` | Manager | History of recorded early repayments |
| GET | `/api/v1/me/demands/{demandId}/credit-file/early-repayments/quote?amount=` | Client | Same quote, for a demand in the caller's portfolio |
| POST | `/api/v1/me/demands/{demandId}/credit-file/installments/{installmentId}/payments` | Client | Self-declare having paid an installment ("J'ai payé"), body `{sourceRib}`. Moves the installment to `DECLARED` and notifies the demand's gestionnaire: no real payment gateway is integrated, a Manager still has to validate/reject it against the actual bank statement |
| GET | `/api/v1/users`, `/api/v1/users/{id}` | Manager | List / view staff and client accounts |
| POST | `/api/v1/users` | Manager | Create a staff account (username, password, role, optional `fullName`/`email`/`gradeCode`/`teamCode`) |
| PATCH | `/api/v1/users/{id}/role` | Manager | Promote or demote a user's role |
| PATCH | `/api/v1/users/{id}/profile` | Manager | Update a user's grade, team and e-mail; a `null` field clears it. Changing one's own profile is refused (`403`) and every change is audited |
| GET | `/api/v1/notifications` | Manager | List the caller's own in-app notifications (paginated, most recent first) |
| GET | `/api/v1/notifications/unread-count` | Manager | Count the caller's unread notifications, backs the header bell's badge |
| GET | `/api/v1/notifications/stream` | Manager | Open a Server-Sent Events connection (`text/event-stream`). Pushes a `notification` event the instant one is created for the caller, so the badge/list updates without polling: see [Notifications](enterprise-credit-workflow.md#notifications-keeping-the-right-people-in-the-loop) |
| PATCH | `/api/v1/notifications/{id}/read` | Manager | Mark one of the caller's own notifications as read |
| PATCH | `/api/v1/notifications/read-all` | Manager | Mark all of the caller's notifications as read |
| GET | `/api/v1/audit-log?entityType=&entityId=` | Manager | Browse the compliance audit trail; with `entityType` (`DEMAND`, `ENTERPRISE`, `USER`) + `entityId`, the full trail of one object, each entry with its field-level `changes` |
| POST | `/api/v1/fake-sms-gateway/sms`, GET `/api/v1/fake-sms-gateway/inbox` | Public, `dev` profile only | Local stand-in for an SMS aggregator, to exercise the real HTTP SMS client end to end |

### Rules of `POST /demands/{id}/advance`

| Rule | Refusal |
|---|---|
| `assigneeUsername` is required for the step being opened, unless the dossier is completing | `422` |
| `rateOption`, `amountOption`, `durationOption` (`KEEP` or `MANUAL`) are mandatory; a `MANUAL` value is re-checked with the simulation's eligibility rules ([details](enterprise-credit-workflow.md#rate-amount-and-duration-a-decision-on-every-advance)) | `422` |
| The applied rate, kept or manual, must stay under the legal ceiling | `422 RATE_ABOVE_LEGAL_MAXIMUM` |
| After the committee decision, `MANUAL` terms are refused until the dossier returns to the decision step | `409 TERMS_FROZEN_AFTER_DECISION` |
| `decisionOutcome=REJECTED` rejects the dossier; `DEFERRED` must come with `returnReasonCode` | `409` |
| On a four-eyes step, whoever closed an earlier step cannot decide | `403 FOUR_EYES_VIOLATION` |
| Only the assignee of a step may act on it | `403 ACTOR_NOT_ASSIGNED_TO_STEP` |
| Past `INSURANCE_VALIDATION_STEP`, `insuranceAdiSubscribed` (and `insuranceDamageSubscribed` when there are guarantees) must be `true`; `insuranceCoverageType` is informational ([details](enterprise-credit-workflow.md#insurance-pricing-and-the-workflow-step-are-two-different-things)) | `422` |

When the committee approves, the pricing parameters in force (fees, insurance, Mourabaha VAT and takaful) are
frozen for the dossier and used for its credit file.

## Error Handling

Every error the API returns is a typed `ApiException`, translated by a centralized
`@RestControllerAdvice` into a consistent JSON body with a `requestId`, `timestamp`, `errorCode`
(e.g. `CB-422`), and a human-readable `description`: no matter which layer raised it.

| Exception | Represents | Example outcomes |
|---|---|---|
| `AuthException` | Login, registration, token and permission failures | `401` invalid credentials / expired or reused refresh token. `409` username already taken. `403` a manager tries to change their own role or profile, or to demote the last remaining manager. `423` account temporarily locked after repeated failed logins. `500` an authenticated user somehow has no role assigned (defensive guard, should never happen in practice) |
| `BAMException` | A simulation rejected by BAM regulation | `422` duration, deposit, age, or debt-ratio outside the allowed range. This is the "Rejected" branch of the [mortgage credit workflow](mortgage-credit-workflow.md#mortgage-credit-simulation-workflow-business-process) |
| `MortgageCreditException` | The mortgage simulation cannot run at all (a configuration problem, not the applicant's fault), or a mortgage demand's amortization recompute is blocked | `404` unknown/inactive CSP. `500` no pricing strategy registered for the requested product. `503` BAM or Mourabaha parameters not configured. `409` `GET /demands/{id}/amortization` on a mortgage-Conventional demand that has never been advanced: no applicable rate yet, Mourabaha never needs one, see [Mortgage Credit Workflow](mortgage-credit-workflow.md#recomputing-the-amortization-schedule-later) |
| `InvestmentCreditException` | An enterprise (investment) credit simulation rejected on eligibility, or its parameters not configured | `422` company age, financing/revenue ratio, guarantee coverage, or debt-service ratio outside the allowed range. This is the "Rejected" branch of the [enterprise credit workflow](enterprise-credit-workflow.md). `503` parameters not configured for the enterprise type. `409` `GET /demands/{id}/amortization` on an enterprise demand that has never been advanced (no applicable rate yet) |
| `EnterpriseException` | An Enterprise (client company) record problem | `404` `GET /enterprises/{id}` or `POST /demands/{id}/classify` (`REAL`) references an unknown `enterpriseId` |
| `EnterpriseUserLinkException` | A client↔Enterprise portfolio-access link problem | `404` link or `registrationNumber` not found. `409` an active (pending/approved) link already exists for that pair, or the link isn't in the state an approve/reject/revoke call requires. `422` invite target isn't a `CLIENT`. `403` `GET /me/portfolio/{id}/demands` without an approved link to that Enterprise |
| `ClientInvitationException` | A client-invitation token problem | `404` no pending invitation for the given token. `409` the invitation has expired |
| `ActivitySectorException` | An activity sector record problem | `409` `POST /activity-sectors` with a name that already exists |
| `WorkflowException` | A problem tracking a demand through the Workflow/Process/Step pipeline | `404` demand not found. `409` demand already completed or rejected, already at its first step (cannot be returned further), still at classification (`MUST_CLASSIFY_FIRST`), or deferred by the committee without a return (`DECISION_DEFERRED_REQUIRES_RETURN`). `422` `assigneeUsername` missing while a step is being opened, a `MANUAL` value missing, a manual rate above the legal maximum (`RATE_ABOVE_LEGAL_MAXIMUM`), or insurance not confirmed. `403` caller/assignee grade-team mismatch (an assigner with **no** team is rejected like one with the wrong team), or four-eyes violation (`FOUR_EYES_VIOLATION`). `503` no active workflow configured for the demand's product/enterprise type. Document upload/download has its own set: `422` file is empty/too large, content type outside the whitelist, or flagged by the antivirus scan; `404` document not found; `409` document hasn't completed its antivirus scan yet |
| `DocumentRequestException` | A document-request problem, either side of a Manager↔Client document exchange | `404` request not found, or found but for a different demand. `409` `GET .../documents/download` before the client has uploaded anything (request still `PENDING`) |
| `CreditFileException` | A Dossier de crédit / installment / payment / early-repayment problem | `404` no credit file opened yet for this demand (it hasn't completed its pipeline), installment not found, or installment doesn't belong to this credit file. `409` installment already paid, already has a declared payment awaiting validation, no declared payment to validate/reject, credit file already closed, or late/unvalidated installments blocking an early repayment. `422` early-repayment amount ≤ 0 or above the outstanding capital, or early repayment on a Mourabaha: see [Credit File Servicing](enterprise-credit-workflow.md#credit-file-servicing-after-the-demand-is-accorded) |
| `DemandImportException` | A bulk demand-import problem | `422` uploaded file is empty, too large, or not a `.xlsx` spreadsheet. `404` no import job for the given execution ID. `409` an import is already running |
| `NotificationException` | A notification doesn't exist, or doesn't belong to the caller | `404` `PATCH /notifications/{id}/read` targeting an unknown id or someone else's notification |
| `BamParametersException` / `CreditRateException` / `MourabahaParametersException` | Manager-facing configuration mistakes | `404` record not found. `400` an invalid range (e.g. minimum processing fee greater than the maximum) |
| `DocumentSuggestionException` | A document-name suggestion problem | `404` suggestion not found. `409` the same label already exists for that scope. `400` both `enterpriseType` and `cspCode` given (a suggestion targets one or the other) |
| `RegulatoryLimitException` | A rate grid edit or a simulation above the legal ceiling (TMIC, `MAX_ANNUAL_RATE`; simulations compared on `MAX_RATE_SIMULATION_BASIS`) | `422` `RATE_ABOVE_LEGAL_MAXIMUM`; the message names the rate and the ceiling, nothing is saved, a refused simulation is audited |
| `ValidationException` | Malformed request | `400` unrecognized field, more than one JSON object in the request body, invalid enum value, or `entityType`/`entityId` given without the other on the audit log. (`page` < 1 or `size` outside 1–50 are rejected by Bean Validation with the same `INVALID_PAGE`/`INVALID_SIZE` messages) |

Every error `description` (including `400`, `401`, `403`, `413` body too large and `429` rate limited) is
written in the language of the request's `Accept-Language` header: French (`fr`, the default), English
(`en`) or Arabic (`ar`); the `errorCode` (`CB-<status>`) and technical names (`INVALID_VALUE`, field names)
stay the same in every language.

Bean Validation failures (`@Valid`) and invalid domain values (e.g. a RIB with a wrong key) also return
`400`; an invalid domain value's message is never echoed back, since it may contain personal data.
