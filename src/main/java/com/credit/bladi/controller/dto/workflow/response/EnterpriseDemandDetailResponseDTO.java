package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "EnterpriseDemandDetailResponseDTO",
    description = "Enterprise-investment-credit-specific figures, captured at simulation time. Present "
        + "only when DemandResponseDTO.demandType is ENTERPRISE. All monetary values are in MAD."
)
public record EnterpriseDemandDetailResponseDTO(
    @Schema(description = "Enterprise category.", example = "PME") String enterpriseType,
    @Schema(description = "Public id of the Enterprise (client company) this demand belongs to: the same id "
        + "GET /api/v1/enterprises/{id} and POST .../classify use. Null until an account officer classifies the "
        + "demand as REAL at CLASSIFICATION_STEP.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    UUID enterpriseId,
    @Schema(description = "Economic sector captured at simulation time.", example = "Textile") String sector,
    @Schema(description = "Company age in years, captured at simulation time.", example = "5")
    Integer companyAgeYears,
    @Schema(description = "Annual revenue (MAD) captured at simulation time.", example = "8000000.00")
    BigDecimal annualRevenue,
    @Schema(description = "Net annual result (MAD) captured at simulation time.", example = "1200000.00")
    BigDecimal netAnnualResult,
    @Schema(description = "Existing annual debt service (MAD) captured at simulation time.", example = "0.00")
    BigDecimal existingAnnualDebtService,
    @Schema(description = "Guarantees value (MAD) offered by the client, captured at simulation time.",
        example = "1000000.00") BigDecimal guaranteesValue
) {}
