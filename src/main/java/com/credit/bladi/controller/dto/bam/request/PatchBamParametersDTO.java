package com.credit.bladi.controller.dto.bam.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "PatchBamParametersDTO",
    description = """
        Request payload to update BAM regulatory parameters. Admin use only.

        Only provided fields are updated (partial update).
        The record to update is identified by the {id} path variable.
        """
)
public class PatchBamParametersDTO {

    @Schema(description = "New processing fees ratio applied to the loan amount.", example = "0.010")
    @DecimalMin(value = "0.0", inclusive = false, message = "processingFeesRatio must be strictly positive")
    @DecimalMax(value = "0.01", message = "processingFeesRatio cannot exceed 0.01 (1%)")
    private BigDecimal processingFeesRatio;

    @Schema(description = "New minimum processing fees amount (MAD).", example = "5000.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "minProcessingFees must be strictly positive")
    private BigDecimal minProcessingFees;

    @Schema(description = "New maximum processing fees amount (MAD).", example = "20000.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "maxProcessingFees must be strictly positive")
    private BigDecimal maxProcessingFees;

    @Schema(description = "New registration fee ratio.", example = "0.015")
    @DecimalMin(value = "0.0", inclusive = false, message = "registrationFeeRatio must be strictly positive")
    @DecimalMax(value = "0.05", message = "registrationFeeRatio cannot exceed 0.05 (5%)")
    private BigDecimal registrationFeeRatio;

    @Schema(description = "New land conservation ratio.", example = "0.015")
    @DecimalMin(value = "0.0", inclusive = false, message = "landConservationRatio must be strictly positive")
    @DecimalMax(value = "0.05", message = "landConservationRatio cannot exceed 0.05 (5%)")
    private BigDecimal landConservationRatio;

    @Schema(description = "New notary fee ratio.", example = "0.010")
    @DecimalMin(value = "0.0", inclusive = false, message = "notaryFeeRatio must be strictly positive")
    @DecimalMax(value = "0.05", message = "notaryFeeRatio cannot exceed 0.05 (5%)")
    private BigDecimal notaryFeeRatio;

    @Schema(description = "New loan-to-value ratio (BAM rule). E.g. 0.9000 means max 90% of property value.",
        example = "0.9000")
    @DecimalMin(value = "0.0", inclusive = false, message = "ltvRatio must be strictly positive")
    @DecimalMax(value = "0.90", message = "ltvRatio cannot exceed 0.90 (90%) — BAM minimum deposit is 10%")
    private BigDecimal ltvRatio;

    @Schema(description = "New maximum debt-to-income ratio (BAM rule). E.g. 0.4000 means max 40% of monthly income.",
        example = "0.4000")
    @DecimalMin(value = "0.0", inclusive = false, message = "maxDebtRatio must be strictly positive")
    @DecimalMax(value = "0.40", message = "maxDebtRatio cannot exceed 0.40 (40%) — BAM regulatory maximum")
    private BigDecimal maxDebtRatio;

    @Schema(description = "New maximum age at credit maturity, enforced for every CSP profile (BAM regulation).",
        example = "75")
    @Min(value = 1, message = "maxAgeAtMaturity must be strictly positive")
    @Max(value = 85, message = "maxAgeAtMaturity cannot exceed 85")
    private Integer maxAgeAtMaturity;

    @Schema(description = "New ADI (death/disability insurance) monthly premium rate applied to the loan capital "
        + "(BAM regulation). E.g. 0.0004 means 0.04% of capital per month.", example = "0.0004")
    @DecimalMin(value = "0.0", inclusive = false, message = "adiMonthlyRate must be strictly positive")
    @DecimalMax(value = "0.001", message = "adiMonthlyRate cannot exceed 0.001 (0.1%)")
    private BigDecimal adiMonthlyRate;

    @Schema(description = "Whether to set this record as the active configuration.", example = "true")
    private Boolean active;

    public BigDecimal getProcessingFeesRatio() {
        return processingFeesRatio;
    }

    public void setProcessingFeesRatio(BigDecimal processingFeesRatio) {
        this.processingFeesRatio = processingFeesRatio;
    }

    public BigDecimal getMinProcessingFees() {
        return minProcessingFees;
    }

    public void setMinProcessingFees(BigDecimal minProcessingFees) {
        this.minProcessingFees = minProcessingFees;
    }

    public BigDecimal getMaxProcessingFees() {
        return maxProcessingFees;
    }

    public void setMaxProcessingFees(BigDecimal maxProcessingFees) {
        this.maxProcessingFees = maxProcessingFees;
    }

    public BigDecimal getRegistrationFeeRatio() {
        return registrationFeeRatio;
    }

    public void setRegistrationFeeRatio(BigDecimal registrationFeeRatio) {
        this.registrationFeeRatio = registrationFeeRatio;
    }

    public BigDecimal getLandConservationRatio() {
        return landConservationRatio;
    }

    public void setLandConservationRatio(BigDecimal landConservationRatio) {
        this.landConservationRatio = landConservationRatio;
    }

    public BigDecimal getNotaryFeeRatio() {
        return notaryFeeRatio;
    }

    public void setNotaryFeeRatio(BigDecimal notaryFeeRatio) {
        this.notaryFeeRatio = notaryFeeRatio;
    }

    public BigDecimal getLtvRatio() {
        return ltvRatio;
    }

    public void setLtvRatio(BigDecimal ltvRatio) {
        this.ltvRatio = ltvRatio;
    }

    public BigDecimal getMaxDebtRatio() {
        return maxDebtRatio;
    }

    public void setMaxDebtRatio(BigDecimal maxDebtRatio) {
        this.maxDebtRatio = maxDebtRatio;
    }

    public Integer getMaxAgeAtMaturity() {
        return maxAgeAtMaturity;
    }

    public void setMaxAgeAtMaturity(Integer maxAgeAtMaturity) {
        this.maxAgeAtMaturity = maxAgeAtMaturity;
    }

    public BigDecimal getAdiMonthlyRate() {
        return adiMonthlyRate;
    }

    public void setAdiMonthlyRate(BigDecimal adiMonthlyRate) {
        this.adiMonthlyRate = adiMonthlyRate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PatchBamParametersDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "PatchBamParametersDTO [processingFeesRatio=%s, minProcessingFees=%s, maxProcessingFees=%s, "
                + "registrationFeeRatio=%s, landConservationRatio=%s, notaryFeeRatio=%s, "
                + "ltvRatio=%s, maxDebtRatio=%s, maxAgeAtMaturity=%s, adiMonthlyRate=%s, active=%s]",
            processingFeesRatio, minProcessingFees, maxProcessingFees,
            registrationFeeRatio, landConservationRatio, notaryFeeRatio,
            ltvRatio, maxDebtRatio, maxAgeAtMaturity, adiMonthlyRate, active);
    }
}