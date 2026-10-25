package com.credit.bladi.controller.dto.creditrate.request;

import com.credit.bladi.validator.ValidationConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Schema(
    name = "PatchCreditRateDTO",
    description = """
        Request payload to update a credit rate record. Admin use only.

        Only provided fields are updated (partial update).
        The record to update is identified by the {id} path variable.
        minDurationMonths and maxDurationMonths must be provided together: supplying only
        one of the two is rejected, since the pair forms a single duration range.
        """
)
public record PatchCreditRateDTO(
    @Schema(description = "New minimum interest rate (%).", example = "4.60")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.creditRate.rateMin.min}")
    @DecimalMax(value = "100.0", message = "{validation.creditRate.rateMin.max}")
    BigDecimal rateMin,

    @Schema(description = "New maximum interest rate (%).", example = "5.50")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.creditRate.rateMax.min}")
    @DecimalMax(value = "100.0", message = "{validation.creditRate.rateMax.max}")
    BigDecimal rateMax,

    @Schema(description = "New minimum loan duration, in months. Must be supplied together with "
        + "maxDurationMonths.", example = "12")
    @Min(value = 0, message = "{validation.creditRate.minDurationMonths.min}")
    Integer minDurationMonths,

    @Schema(description = "New maximum loan duration, in months. Must be supplied together with "
        + "minDurationMonths.", example = "240")
    @Min(value = 0, message = "{validation.creditRate.maxDurationMonths.min}")
    Integer maxDurationMonths,

    @Schema(description = "New rate calculation formula applied to rateMin/rateMax for this profile.",
        example = "AVERAGE")
    @Pattern(regexp = ValidationConstraints.RATE_FORMULA_REGEX, message = "{validation.creditRate.rateFormula.pattern}")
    String rateFormula,

    @Schema(description = "Whether to set this record as the active rate for its CSP code.", example = "true")
    Boolean active
) {}
