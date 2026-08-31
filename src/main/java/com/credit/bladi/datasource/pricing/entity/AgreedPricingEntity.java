package com.credit.bladi.datasource.pricing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "demand_agreed_pricing")
public class AgreedPricingEntity {
    @Id
    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "processing_fees_ratio", precision = 10, scale = 6)
    private BigDecimal processingFeesRatio;

    @Column(name = "min_processing_fees", precision = 15, scale = 2)
    private BigDecimal minProcessingFees;

    @Column(name = "max_processing_fees", precision = 15, scale = 2)
    private BigDecimal maxProcessingFees;

    @Column(name = "registration_fee_ratio", precision = 10, scale = 6)
    private BigDecimal registrationFeeRatio;

    @Column(name = "land_conservation_ratio", precision = 10, scale = 6)
    private BigDecimal landConservationRatio;

    @Column(name = "notary_fee_ratio", precision = 10, scale = 6)
    private BigDecimal notaryFeeRatio;

    @Column(name = "adi_monthly_rate", precision = 10, scale = 6)
    private BigDecimal adiMonthlyRate;

    @Column(name = "vat_rate", precision = 10, scale = 6)
    private BigDecimal vatRate;

    @Column(name = "takaful_monthly_rate", precision = 10, scale = 6)
    private BigDecimal takafulMonthlyRate;

    @Column(name = "investment_processing_fees_ratio", precision = 10, scale = 6)
    private BigDecimal investmentProcessingFeesRatio;

    @Column(name = "investment_insurance_premium_ratio", precision = 10, scale = 6)
    private BigDecimal investmentInsurancePremiumRatio;

    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    @Column(name = "captured_by", length = 100)
    private String capturedBy;

    public AgreedPricingEntity() {
    }

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
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

    public BigDecimal getAdiMonthlyRate() {
        return adiMonthlyRate;
    }

    public void setAdiMonthlyRate(BigDecimal adiMonthlyRate) {
        this.adiMonthlyRate = adiMonthlyRate;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getTakafulMonthlyRate() {
        return takafulMonthlyRate;
    }

    public void setTakafulMonthlyRate(BigDecimal takafulMonthlyRate) {
        this.takafulMonthlyRate = takafulMonthlyRate;
    }

    public BigDecimal getInvestmentProcessingFeesRatio() {
        return investmentProcessingFeesRatio;
    }

    public void setInvestmentProcessingFeesRatio(BigDecimal investmentProcessingFeesRatio) {
        this.investmentProcessingFeesRatio = investmentProcessingFeesRatio;
    }

    public BigDecimal getInvestmentInsurancePremiumRatio() {
        return investmentInsurancePremiumRatio;
    }

    public void setInvestmentInsurancePremiumRatio(BigDecimal investmentInsurancePremiumRatio) {
        this.investmentInsurancePremiumRatio = investmentInsurancePremiumRatio;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(Instant capturedAt) {
        this.capturedAt = capturedAt;
    }

    public String getCapturedBy() {
        return capturedBy;
    }

    public void setCapturedBy(String capturedBy) {
        this.capturedBy = capturedBy;
    }
}
