package com.credit.bladi.controller.dto.bam.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(
    name = "BamParametersResponseDTO",
    description = "BAM regulatory parameters record used to compute mortgage fees and rates."
)
public record BamParametersResponseDTO(
    @Schema(description = "Record identifier.", example = "3f2a9c1b-6e4d-4a2f-9c1b-6e4d4a2f9c1b") UUID id,
    @Schema(description = "Processing fees ratio applied to the loan amount.", example = "0.010")
    BigDecimal processingFeesRatio,
    @Schema(description = "Minimum processing fees amount (MAD).", example = "5000.00")
    BigDecimal minProcessingFees,
    @Schema(description = "Maximum processing fees amount (MAD).", example = "20000.00")
    BigDecimal maxProcessingFees,
    @Schema(description = "Registration fee ratio.", example = "0.015") BigDecimal registrationFeeRatio,
    @Schema(description = "Land conservation ratio.", example = "0.015") BigDecimal landConservationRatio,
    @Schema(description = "Notary fee ratio.", example = "0.010") BigDecimal notaryFeeRatio,
    @Schema(description = "Loan-to-value ratio (BAM rule). Max % of property value that can be borrowed.",
        example = "0.9000") BigDecimal ltvRatio,
    @Schema(description = "Maximum debt-to-income ratio (BAM rule). Max % of monthly income for repayment.",
        example = "0.4000") BigDecimal maxDebtRatio,
    @Schema(description = "Maximum age at credit maturity, enforced for every CSP profile (BAM regulation).",
        example = "75") Integer maxAgeAtMaturity,
    @Schema(description = "ADI (death/disability insurance) monthly premium rate applied to the loan capital "
        + "(BAM regulation). E.g. 0.0004 means 0.04% of capital per month.", example = "0.0004")
    BigDecimal adiMonthlyRate,
    @Schema(description = "Whether this record is the active configuration.", example = "true") Boolean active
) {}
