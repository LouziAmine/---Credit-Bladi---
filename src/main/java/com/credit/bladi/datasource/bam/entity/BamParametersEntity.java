package com.credit.bladi.datasource.bam.entity;

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
@Table(name = "bam_parameters")
public class BamParametersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "processing_fees_ratio", nullable = false, precision = 6, scale = 4)
    private BigDecimal processingFeesRatio;

    @Column(name = "min_processing_fees", nullable = false, precision = 10, scale = 2)
    private BigDecimal minProcessingFees;

    @Column(name = "max_processing_fees", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxProcessingFees;

    @Column(name = "registration_fee_ratio", nullable = false, precision = 6, scale = 4)
    private BigDecimal registrationFeeRatio;

    @Column(name = "land_conservation_ratio", nullable = false, precision = 6, scale = 4)
    private BigDecimal landConservationRatio;

    @Column(name = "notary_fee_ratio", nullable = false, precision = 6, scale = 4)
    private BigDecimal notaryFeeRatio;

    @Column(name = "ltv_ratio", nullable = false, precision = 5, scale = 4)
    private BigDecimal ltvRatio;

    @Column(name = "max_debt_ratio", nullable = false, precision = 5, scale = 4)
    private BigDecimal maxDebtRatio;

    @Column(name = "max_age_at_maturity", nullable = false)
    private Integer maxAgeAtMaturity;

    @Column(name = "adi_monthly_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal adiMonthlyRate;

    @Column(name = "active")
    private Boolean active;

    @Version
    @Column(name = "version")
    private Long version;

    public BamParametersEntity() {
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

    public void merge(BamParametersEntity other) {
        MergeSupport.updateIfDifferent(this::setProcessingFeesRatio, this.processingFeesRatio,
            other.getProcessingFeesRatio());
        MergeSupport.updateIfDifferent(this::setMinProcessingFees, this.minProcessingFees,
            other.getMinProcessingFees());
        MergeSupport.updateIfDifferent(this::setMaxProcessingFees, this.maxProcessingFees,
            other.getMaxProcessingFees());
        MergeSupport.updateIfDifferent(this::setRegistrationFeeRatio, this.registrationFeeRatio,
            other.getRegistrationFeeRatio());
        MergeSupport.updateIfDifferent(this::setLandConservationRatio, this.landConservationRatio,
            other.getLandConservationRatio());
        MergeSupport.updateIfDifferent(this::setNotaryFeeRatio, this.notaryFeeRatio, other.getNotaryFeeRatio());
        MergeSupport.updateIfDifferent(this::setLtvRatio, this.ltvRatio, other.getLtvRatio());
        MergeSupport.updateIfDifferent(this::setMaxDebtRatio, this.maxDebtRatio, other.getMaxDebtRatio());
        MergeSupport.updateIfDifferent(this::setMaxAgeAtMaturity, this.maxAgeAtMaturity, other.getMaxAgeAtMaturity());
        MergeSupport.updateIfDifferent(this::setAdiMonthlyRate, this.adiMonthlyRate, other.getAdiMonthlyRate());
        MergeSupport.updateIfDifferent(this::setActive, this.active, other.getActive());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BamParametersEntity other)) {
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
            "BamParametersEntity [id=%s, processingFeesRatio=%s, minProcessingFees=%s, maxProcessingFees=%s, "
                + "registrationFeeRatio=%s, landConservationRatio=%s, notaryFeeRatio=%s, "
                + "ltvRatio=%s, maxDebtRatio=%s, active=%s]",
            id, processingFeesRatio, minProcessingFees, maxProcessingFees,
            registrationFeeRatio, landConservationRatio, notaryFeeRatio,
            ltvRatio, maxDebtRatio, active);
    }
}
