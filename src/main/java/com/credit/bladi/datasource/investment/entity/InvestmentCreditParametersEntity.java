package com.credit.bladi.datasource.investment.entity;

import com.credit.bladi.common.MergeSupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "enterprise_credit_parameters")
public class InvestmentCreditParametersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "enterprise_type", nullable = false, length = 10)
    private String enterpriseType;

    @Column(name = "rate_min", precision = 5, scale = 2)
    private BigDecimal rateMin;

    @Column(name = "rate_max", precision = 5, scale = 2)
    private BigDecimal rateMax;

    @Column(name = "rate_formula", nullable = false, length = 10)
    private String rateFormula;

    @Column(name = "min_company_age_years")
    private Integer minCompanyAgeYears;

    @Column(name = "max_financing_to_revenue_ratio", precision = 5, scale = 4)
    private BigDecimal maxFinancingToRevenueRatio;

    @Column(name = "min_guarantee_coverage_ratio", precision = 5, scale = 4)
    private BigDecimal minGuaranteeCoverageRatio;

    @Column(name = "max_debt_service_ratio", precision = 5, scale = 4)
    private BigDecimal maxDebtServiceRatio;

    @Column(name = "processing_fees_ratio", precision = 6, scale = 4)
    private BigDecimal processingFeesRatio;

    @Column(name = "insurance_premium_ratio", precision = 6, scale = 4)
    private BigDecimal insurancePremiumRatio;

    @Column(name = "active")
    private Boolean active;

    @Version
    @Column(name = "version")
    private Long version;

    public InvestmentCreditParametersEntity() {
    }

    @PrePersist
    private void generatePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public BigDecimal getRateMin() {
        return rateMin;
    }

    public void setRateMin(BigDecimal rateMin) {
        this.rateMin = rateMin;
    }

    public BigDecimal getRateMax() {
        return rateMax;
    }

    public void setRateMax(BigDecimal rateMax) {
        this.rateMax = rateMax;
    }

    public String getRateFormula() {
        return rateFormula;
    }

    public void setRateFormula(String rateFormula) {
        this.rateFormula = rateFormula;
    }

    public Integer getMinCompanyAgeYears() {
        return minCompanyAgeYears;
    }

    public void setMinCompanyAgeYears(Integer minCompanyAgeYears) {
        this.minCompanyAgeYears = minCompanyAgeYears;
    }

    public BigDecimal getMaxFinancingToRevenueRatio() {
        return maxFinancingToRevenueRatio;
    }

    public void setMaxFinancingToRevenueRatio(BigDecimal maxFinancingToRevenueRatio) {
        this.maxFinancingToRevenueRatio = maxFinancingToRevenueRatio;
    }

    public BigDecimal getMinGuaranteeCoverageRatio() {
        return minGuaranteeCoverageRatio;
    }

    public void setMinGuaranteeCoverageRatio(BigDecimal minGuaranteeCoverageRatio) {
        this.minGuaranteeCoverageRatio = minGuaranteeCoverageRatio;
    }

    public BigDecimal getMaxDebtServiceRatio() {
        return maxDebtServiceRatio;
    }

    public void setMaxDebtServiceRatio(BigDecimal maxDebtServiceRatio) {
        this.maxDebtServiceRatio = maxDebtServiceRatio;
    }

    public BigDecimal getProcessingFeesRatio() {
        return processingFeesRatio;
    }

    public void setProcessingFeesRatio(BigDecimal processingFeesRatio) {
        this.processingFeesRatio = processingFeesRatio;
    }

    public BigDecimal getInsurancePremiumRatio() {
        return insurancePremiumRatio;
    }

    public void setInsurancePremiumRatio(BigDecimal insurancePremiumRatio) {
        this.insurancePremiumRatio = insurancePremiumRatio;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void merge(InvestmentCreditParametersEntity other) {
        MergeSupport.updateIfDifferent(this::setRateMin, this.rateMin, other.getRateMin());
        MergeSupport.updateIfDifferent(this::setRateMax, this.rateMax, other.getRateMax());
        MergeSupport.updateIfDifferent(this::setRateFormula, this.rateFormula, other.getRateFormula());
        MergeSupport.updateIfDifferent(this::setMinCompanyAgeYears, this.minCompanyAgeYears,
            other.getMinCompanyAgeYears());
        MergeSupport.updateIfDifferent(this::setMaxFinancingToRevenueRatio,
            this.maxFinancingToRevenueRatio, other.getMaxFinancingToRevenueRatio());
        MergeSupport.updateIfDifferent(this::setMinGuaranteeCoverageRatio,
            this.minGuaranteeCoverageRatio, other.getMinGuaranteeCoverageRatio());
        MergeSupport.updateIfDifferent(this::setMaxDebtServiceRatio, this.maxDebtServiceRatio,
            other.getMaxDebtServiceRatio());
        MergeSupport.updateIfDifferent(this::setProcessingFeesRatio, this.processingFeesRatio,
            other.getProcessingFeesRatio());
        MergeSupport.updateIfDifferent(this::setInsurancePremiumRatio,
            this.insurancePremiumRatio, other.getInsurancePremiumRatio());
        MergeSupport.updateIfDifferent(this::setActive, this.active, other.getActive());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InvestmentCreditParametersEntity other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "InvestmentCreditParametersEntity [id=%s, enterpriseType=%s, rateMin=%s, rateMax=%s, "
                + "rateFormula=%s, active=%s]",
            id, enterpriseType, rateMin, rateMax, rateFormula, active);
    }
}
