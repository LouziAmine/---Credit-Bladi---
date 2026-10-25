package com.credit.bladi.controller.dto.enterprise.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "ClientDemandSummaryResponseDTO",
    description = "A deliberately trimmed, client-safe view of a Demand belonging to the caller's linked "
        + "Enterprise: status and key figures only, never the internal assignee/comment/step-history "
        + "detail a Manager sees (least-privilege scoped read).")
public record ClientDemandSummaryResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Human-readable demand reference.", example = "DEM-3F2A9C1B") String reference,
    @Schema(description = "Enterprise category.", example = "PME") String enterpriseType,
    @Schema(description = "Requested financing amount (MAD).", example = "1500000.00") BigDecimal amount,
    @Schema(description = "Financing duration (months).", example = "84") Integer duration,
    @Schema(description = "Current applicable interest rate (%), null before a first advance sets one.",
        example = "7.25") BigDecimal applicableRate,
    @Schema(description = "When this demand was created.") Instant createdAt,
    @Schema(description = "When this demand completed its pipeline (null while still in progress).")
    Instant completedAt,
    @Schema(description = "Whether this demand has completed its full processing pipeline.", example = "false")
    Boolean completed,
    @Schema(description = "Whether this demand was rejected at classification.", example = "false")
    Boolean rejected
) {}
