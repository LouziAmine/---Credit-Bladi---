package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "DemandResponseDTO", description = "A credit demand (enterprise investment or individual "
    + "mortgage) tracked through the Workflow/Process/Step processing pipeline. Exactly one of "
    + "`enterprise`/`mortgage` is present, matching `demandType`.")
public record DemandResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Human-readable demand reference.", example = "DEM-3F2A9C1B") String reference,
    @Schema(description = "Which product this demand belongs to: drives which of enterprise/mortgage "
        + "below is populated.", example = "ENTERPRISE") String demandType,
    @Schema(description = "Applicant's display name: company legal name for ENTERPRISE demands, "
        + "individual's name for MORTGAGE demands.", example = "Atlas Textile SARL") String contactName,
    @Schema(description = "Requested financing amount (MAD).", example = "1500000.00") BigDecimal amount,
    @Schema(description = "Financing duration (months).", example = "84") Integer duration,
    @Schema(description = "When this demand was created.") Instant createdAt,
    @Schema(description = "When this demand completed its pipeline (null while still in progress).")
    Instant completedAt,
    @Schema(description = "Whether this demand has completed its full processing pipeline.", example = "false")
    Boolean completed,
    @Schema(description = "Credit's current applicable interest rate (%). Null only for demands created "
        + "before this field existed that have not yet been advanced (their first advance forces "
        + "MANUAL: KEEP is unavailable while this is null).", example = "7.25") BigDecimal applicableRate,
    @Schema(description = "Contact email captured at simulation time.", example = "contact@atlas-textile.ma")
    String contactEmail,
    @Schema(description = "Contact phone number captured at simulation time.", example = "0612345678")
    String contactPhone,
    @Schema(description = "Process name of the demand's current pipeline step (null once completed with no open "
        + "step, or on endpoints that don't batch-load it). Populated on the list endpoint.",
        example = "Analyse contre-risque") String currentProcessName,
    @Schema(description = "Step name of the demand's current pipeline step (null once completed with no open "
        + "step, or on endpoints that don't batch-load it). Populated on the list endpoint.",
        example = "Traitement - Analyse contre-risque") String currentStepName,
    @Schema(description = "Whether this demand was rejected at classification (FAKE or SIMULATION_ONLY) "
        + "and will never enter the real pipeline. Mutually exclusive with completed.", example = "false")
    Boolean rejected,
    @Schema(description = "When this demand was rejected at classification (null unless rejected).")
    Instant rejectedAt,
    @Schema(description = "Enterprise-specific figures: present when demandType is ENTERPRISE, null "
        + "otherwise.") EnterpriseDemandDetailResponseDTO enterprise,
    @Schema(description = "Mortgage-specific figures: present when demandType is MORTGAGE, null otherwise.")
    MortgageDemandDetailResponseDTO mortgage
) {}
