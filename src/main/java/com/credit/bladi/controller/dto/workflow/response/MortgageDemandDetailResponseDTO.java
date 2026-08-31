package com.credit.bladi.controller.dto.workflow.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(
    name = "MortgageDemandDetailResponseDTO",
    description = "Mortgage-specific figures, captured at simulation time. Present only when "
        + "DemandResponseDTO.demandType is MORTGAGE. All monetary values are in MAD."
)
public record MortgageDemandDetailResponseDTO(
    @Schema(description = "Mortgage product.", example = "CONVENTIONAL") String productType,
    @Schema(description = "Applicant's professional category (CSP).", example = "SALA") String cspCode,
    @Schema(description = "Property value (MAD) captured at simulation time.", example = "1800000.00")
    BigDecimal propertyValue,
    @Schema(description = "Applicant's monthly income (MAD) captured at simulation time.", example = "18000.00")
    BigDecimal monthlyIncome
) {}
