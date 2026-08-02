package com.credit.bladi.controller.dto.bam.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "BamParametersResponseDTO",
    description = "BAM regulatory parameters record used to compute mortgage fees and rates."
)
public class BamParametersResponseDTO {

    @Schema(description = "Record identifier.", example = "1")
    private Long id;

    @Schema(description = "Processing fees ratio applied to the loan amount.", example = "0.010")
    private BigDecimal processingFeesRatio;

    @Schema(description = "Minimum processing fees amount (MAD).", example = "5000.00")
    private BigDecimal minProcessingFees;

    @Schema(description = "Maximum processing fees amount (MAD).", example = "20000.00")
    private BigDecimal maxProcessingFees;

    @Schema(description = "Registration fee ratio.", example = "0.015")
    private BigDecimal registrationFeeRatio;

    @Schema(description = "Land conservation ratio.", example = "0.015")
    private BigDecimal landConservationRatio;

    @Schema(description = "Notary fee ratio.", example = "0.010")
    private BigDecimal notaryFeeRatio;

    @Schema(description = "Loan-to-value ratio (BAM rule). Max % of property value that can be borrowed.",
        example = "0.9000")
    private BigDecimal ltvRatio;

    @Schema(description = "Maximum debt-to-income ratio (BAM rule). Max % of monthly income for repayment.",
        example = "0.4000")
    private BigDecimal maxDebtRatio;

    @Schema(description = "Maximum age at credit maturity, enforced for every CSP profile (BAM regulation).",
        example = "75")
    private Integer maxAgeAtMaturity;

    @Schema(description = "ADI (death/disability insurance) monthly premium rate applied to the loan capital "
        + "(BAM regulation). E.g. 0.0004 means 0.04% of capital per month.", example = "0.0004")
    private BigDecimal adiMonthlyRate;

    @Schema(description = "Whether this record is the active configuration.", example = "true")
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return o instanceof BamParametersResponseDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "BamParametersResponseDTO [id=%s, processingFeesRatio=%s, minProcessingFees=%s, maxProcessingFees=%s, "
                + "registrationFeeRatio=%s, landConservationRatio=%s, notaryFeeRatio=%s, "
                + "ltvRatio=%s, maxDebtRatio=%s, maxAgeAtMaturity=%s, active=%s]",
            id, processingFeesRatio, minProcessingFees, maxProcessingFees,
            registrationFeeRatio, landConservationRatio, notaryFeeRatio,
            ltvRatio, maxDebtRatio, maxAgeAtMaturity, active);
    }
}