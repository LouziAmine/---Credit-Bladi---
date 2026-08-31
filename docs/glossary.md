[Back to README](../README.md)

# Glossary

When a ticket says "the dossier is stuck at classification" or "the manager can't validate a payment", this
page tells you which class to open. It is a shortlist, not a data dictionary. Read
[Naming traps](#naming-traps) before searching for a word that "should" exist and doesn't.

## Business word to code

| Business word | In the code | Where |
|---|---|---|
| Simulation | `simulate(...)` | `MortgageCreditService`, `InvestmentCreditService`; `POST /api/v1/mortgage-credit`, `POST /api/v1/investment-credit` |
| Dossier, while it is instructed | `DemandModel`, `Demand` (aggregate), table `demand` | `model/workflow/`, `WorkflowEngineService` |
| Dossier, once approved and in repayment | `CreditFileModel`, table `credit_file` | `model/servicing/`, `CreditFileService` |
| Échéance (one installment) | `InstallmentModel`: `PENDING`, `DECLARED`, `PAID`, `LATE`, `REPAID_EARLY` | `model/servicing/` |
| Intérêts de retard | `LateInterestPolicy` | `model/servicing/`, `CreditFileService` |
| Remboursement anticipé | `EarlyRepaymentService`, `EarlyRepaymentCalculator`, `EarlyRepaymentPolicy` | `service/servicing/`, `model/servicing/` |
| Dossier soldé | `CreditFileStatus.CLOSED` | `CreditFileModel#isActive` |
| Faire avancer un dossier | `advance(...)` | `WorkflowEngineService#advance` |
| Décision du comité : accordé, ajourné, refusé | `DecisionOutcome`: `APPROVED`, `DEFERRED`, `REJECTED` | `StepClosureDetails` |
| Garder ou changer le taux, le montant, la durée | `KeepOrManualOption` (see the traps) | `StepClosureDetails` |
| Conditions figées après la décision | `AgreedPricing` (table `demand_agreed_pricing`), `TERMS_FROZEN_AFTER_DECISION` | `model/pricing/`, `service/pricing/AgreedPricingCapture` |
| Principe des quatre yeux | `requires_four_eyes`, `FOUR_EYES_VIOLATION` | `StepAssignmentPolicy` |
| Qui peut traiter une étape (grade, équipe, superviseur) | `StepAssignmentPolicy` | `service/workflow/access/` |
| Taux maximum légal (TMIC) | `RegulatoryLimits`, `MAX_ANNUAL_RATE`, `RateBasis` | `model/parameters/`, `config/regulatory/` |
| Classer un dossier (réel, faux, simulation seule) | `classify(...)`, `ClassificationOutcome` | `WorkflowEngineService#classify` |
| Le client déclare un paiement | `declarePayment(...)` | `CreditFileService` |
| Le gestionnaire valide ou rejette le paiement | `validatePayment(...)`, `rejectPayment(...)` | `CreditFileService` |
| Crédit immobilier | `Mortgage*` | `service/mortgage/`, `controller/mortgage/` |
| Crédit d'investissement | `Investment*` and `Enterprise*` (see the traps) | `service/investment/`, `EnterpriseDemandDetail` |
| Fiche entreprise (raison sociale, RC, secteur, RIB) | `EnterpriseModel` | `client` package (see the traps) |
| Accès d'un client à son entreprise | `EnterpriseUserLinkModel` | `enterprise` package |
| Emprunteur d'un crédit immobilier (accès portail) | `MortgageBorrowerModel` | `borrower` packages |
| Taille d'entreprise (TPE, auto-entrepreneur, PME, ETI, GE) | `EnterpriseType` | `model/enums/` |
| Catégorie socio-professionnelle | `CspCode` | `model/enums/` |
| TEG | `taeg` field, `TaegCalculationEngine` | `service/calculation/` |
| Mensualité, tableau d'amortissement | `RateCalculationEngine`, `AmortizationRow` | `service/calculation/`, `model/vo/` |
| RIB | `Rib` (Moroccan mod-97 key), `PaymentBeneficiary` | `model/vo/Rib.java` |
| Langue du client | `Language`, `preferredLanguage` (account), `contactLanguage` (dossier) | taken from `Accept-Language`; `NotificationTexts#render` |
| Même personne, nouvelle simulation | `openDemandFromSimulation`, audit `DEMAND_REFRESHED_BY_SIMULATION` | `WorkflowEngineService` |
| Import Excel de dossiers | demand import job | `batch/demand/`, `DemandImportService` |
| Historique "qui a fait quoi" | `AuditService`, `AuditAction`, `AuditTarget`, `AuditChanges` | `GET /audit-log?entityType=DEMAND&entityId=` |
| Contrôle BAM / SCIP | `BamVerificationService` | a mock gateway for now (`/api/v1/mock/verification-bam`) |

The business rules behind these words are in [Mortgage Credit](mortgage-credit-workflow.md) and
[Enterprise Credit](enterprise-credit-workflow.md); the code paths are in [Feature Flows](flows/README.md).

## Naming traps

**"Enterprise" means three things.**

1. `EnterpriseModel`, `EnterpriseController`, `EnterpriseService`: the company record. It lives in the `client`
   package, not `enterprise`.
2. `DemandType.ENTERPRISE`, `EnterpriseDemandDetail`: the investment credit product. Everywhere else this
   product is called `Investment*`.
3. `EnterpriseUserLinkModel`, `EnterpriseUserLinkController`: a client's access to a company. This one does
   live in `enterprise`.

So: the company record is in `client`, the investment product is `Investment*` for pricing and `Enterprise*`
for the dossier type, and portal access is in `enterprise`.

**"Dossier" is two classes.** Users only ever see "dossier". In the code it is a `DemandModel` while it is
instructed, then a `CreditFileModel` once it is approved and the funds are out. The hand-over happens on
`DemandCompletedEvent`. Searching for "dossier" finds neither class.

**`KeepOrManualOption` is named after the form, not the decision.** At each step the rate, amount and duration
are either kept (`KEEP`) or changed (`MANUAL`), which is really the committee's review. After the committee
decision, `MANUAL` is refused.

**Package suffixes.** The BAM and Mourabaha parameter services are in `service/bamparameters` and
`service/mourabahaparameters`; every other layer uses `bam` and `mourabaha`.

## Still lost?

Search for the plain word first: most areas (mortgage, investment, mourabaha, creditrate, workflow, servicing,
audit) use the same package name in every layer, from controller to datasource. The traps above are the
exceptions. Found a new one? Add it here.
