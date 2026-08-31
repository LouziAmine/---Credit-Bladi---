package com.credit.bladi.controller.dto.bam.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Schema(
    name = "PatchBamParametersDTO",
    description = """
        Request payload to update BAM regulatory parameters. Admin use only.

        Only provided fields are updated (partial update).
        The record to update is identified by the {id} path variable.
        """
)
public record PatchBamParametersDTO(
    @Schema(description = "New processing fees ratio applied to the loan amount.", example = "0.010")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.processingFeesRatio.min}")
    @DecimalMax(value = "0.01", message = "{validation.bamParameters.processingFeesRatio.max}")
    BigDecimal processingFeesRatio,

    @Schema(description = "New minimum processing fees amount (MAD).", example = "5000.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.minProcessingFees.min}")
    BigDecimal minProcessingFees,

    @Schema(description = "New maximum processing fees amount (MAD).", example = "20000.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.maxProcessingFees.min}")
    BigDecimal maxProcessingFees,

    @Schema(description = "New registration fee ratio.", example = "0.015")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.registrationFeeRatio.min}")
    @DecimalMax(value = "0.05", message = "{validation.bamParameters.registrationFeeRatio.max}")
    BigDecimal registrationFeeRatio,

    @Schema(description = "New land conservation ratio.", example = "0.015")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.landConservationRatio.min}")
    @DecimalMax(value = "0.05", message = "{validation.bamParameters.landConservationRatio.max}")
    BigDecimal landConservationRatio,

    @Schema(description = "New notary fee ratio.", example = "0.010")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.notaryFeeRatio.min}")
    @DecimalMax(value = "0.05", message = "{validation.bamParameters.notaryFeeRatio.max}")
    BigDecimal notaryFeeRatio,

    @Schema(description = "New loan-to-value ratio (BAM rule). E.g. 0.9000 means max 90% of property value.",
        example = "0.9000")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.ltvRatio.min}")
    @DecimalMax(value = "0.90", message = "{validation.bamParameters.ltvRatio.max}")
    BigDecimal ltvRatio,

    @Schema(description = "New maximum debt-to-income ratio (BAM rule). E.g. 0.4000 means max 40% of monthly income.",
        example = "0.4000")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.maxDebtRatio.min}")
    @DecimalMax(value = "0.40", message = "{validation.bamParameters.maxDebtRatio.max}")
    BigDecimal maxDebtRatio,

    @Schema(description = "New maximum age at credit maturity, enforced for every CSP profile (BAM regulation).",
        example = "75")
    @Min(value = 1, message = "{validation.bamParameters.maxAgeAtMaturity.min}")
    @Max(value = 85, message = "{validation.bamParameters.maxAgeAtMaturity.max}")
    Integer maxAgeAtMaturity,

    @Schema(description = "New ADI (death/disability insurance) monthly premium rate applied to the loan capital "
        + "(BAM regulation). E.g. 0.0004 means 0.04% of capital per month.", example = "0.0004")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.bamParameters.adiMonthlyRate.min}")
    @DecimalMax(value = "0.001", message = "{validation.bamParameters.adiMonthlyRate.max}")
    BigDecimal adiMonthlyRate,

    @Schema(description = "Whether to set this record as the active configuration.", example = "true")
    Boolean active
) {}
