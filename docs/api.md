[← Back to README](../README.md)

# API Endpoints

Full request/response schemas, field constraints and examples are in Swagger UI (see
[Swagger UI](../README.md#swagger-ui) in the README). This table is the quick map of what exists
and who can call it.

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Create a client account |
| POST | `/api/v1/auth/login` | Public | Authenticate; issues access + refresh JWTs as HttpOnly cookies |
| POST | `/api/v1/auth/refresh` | Public | Rotate the access token using the refresh token |
| POST | `/api/v1/auth/logout` | Public | Revoke the current refresh token |
| GET | `/api/v1/auth/me` | Authenticated | Return the current user's username and role |
| POST | `/api/v1/simulation` | Public | Run a mortgage simulation, Conventional or Mourabaha (`productType`), see [Simulation Workflow](business-workflow.md#simulation-workflow-business-process) |
| GET | `/api/v1/bam-parameters`, `/api/v1/bam-parameters/{id}` | Manager | View BAM regulatory parameters (deposit, debt ratio, age, insurance, fee ceilings) |
| PATCH | `/api/v1/bam-parameters/{id}` | Manager | Update BAM regulatory parameters |
| GET | `/api/v1/credit-rates`, `/api/v1/credit-rates/{id}` | Manager | View Conventional credit rates per CSP profile |
| PATCH | `/api/v1/credit-rates/{id}` | Manager | Update a Conventional credit rate |
| GET | `/api/v1/mourabaha-parameters`, `/api/v1/mourabaha-parameters/{id}` | Manager | View Mourabaha profit-margin and VAT parameters |
| PATCH | `/api/v1/mourabaha-parameters/{id}` | Manager | Update Mourabaha profit-margin and VAT parameters |
| GET | `/api/v1/users`, `/api/v1/users/{id}` | Manager | List / view staff and client accounts |
| POST | `/api/v1/users` | Manager | Create a staff account |
| PATCH | `/api/v1/users/{id}/role` | Manager | Promote or demote a user's role |
| GET | `/api/v1/audit-log` | Manager | Browse the compliance audit trail |

## Error Handling

Every error the API returns is a typed `ApiException`, translated by a centralized
`@RestControllerAdvice` into a consistent JSON body with a `requestId`, `timestamp`, `errorCode`
(e.g. `CB-422`), and a human-readable `description`, no matter which layer raised it.

| Exception | Represents | Example outcomes |
|---|---|---|
| `AuthException` | Login, registration, token and permission failures | `401` invalid credentials / expired or reused refresh token, `409` username already taken, `403` a manager tries to change their own role or demote the last remaining manager, `423` account temporarily locked after repeated failed logins, `500` an authenticated user somehow has no role assigned (defensive guard, should never happen in practice) |
| `BAMException` | A simulation rejected by BAM regulation | `422` duration, deposit, age, or debt-ratio outside the allowed range: this is the "Rejected" branch of the [simulation workflow](business-workflow.md#simulation-workflow-business-process) |
| `SimulationException` | The simulation cannot run at all (a configuration problem, not the applicant's fault) | `404` unknown/inactive CSP, `503` BAM or Mourabaha parameters not configured, `500` no pricing strategy registered for the requested product |
| `BamParametersException` / `CreditRateException` / `MourabahaParametersException` | Manager-facing configuration mistakes | `404` record not found, `400` an invalid range (e.g. minimum processing fee greater than the maximum) |
| `ValidationException` | Malformed request | `400` unrecognized field, more than one JSON object in the request body, invalid page/size |

This table serves two audiences: engineers integrating with the API need it to branch on
`errorCode`/HTTP status, and business reviewers can use it to confirm every BAM rejection reason
described above is actually enforced in code, not just documented.