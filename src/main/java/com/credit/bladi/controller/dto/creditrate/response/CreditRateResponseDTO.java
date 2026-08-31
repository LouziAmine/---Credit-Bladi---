package com.credit.bladi.controller.dto.creditrate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "CreditRateResponseDTO",
    description = "Credit rate record used to compute mortgage installments for a given CSP profile."
)
public record CreditRateResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "CSP (socio-professional category) code this rate applies to.", example = "SALA")
    String cspCode,
    @Schema(description = "Minimum interest rate (%).", example = "4.60") BigDecimal rateMin,
    @Schema(description = "Maximum interest rate (%).", example = "5.50") BigDecimal rateMax,
    @Schema(description = "Rate calculation formula applied to rateMin/rateMax for this profile.",
        example = "AVERAGE") String rateFormula,
    @Schema(description = "Minimum loan duration, in months.", example = "12") Integer minDurationMonths,
    @Schema(description = "Maximum loan duration, in months.", example = "240") Integer maxDurationMonths,
    @Schema(description = "Whether this record is the active rate for its CSP code.", example = "true")
    Boolean active
) {}
