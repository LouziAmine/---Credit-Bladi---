package com.credit.bladi.datasource.creditrate.entity;

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
@Table(name = "credit_rate")
public class CreditRateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "csp_code", nullable = false, length = 10)
    private String cspCode;

    @Column(name = "rate_min", precision = 5, scale = 2)
    private BigDecimal rateMin;

    @Column(name = "rate_max", precision = 5, scale = 2)
    private BigDecimal rateMax;

    @Column(name = "rate_formula", nullable = false, length = 10)
    private String rateFormula;

    @Column(name = "min_duration_months")
    private Integer minDurationMonths;

    @Column(name = "max_duration_months")
    private Integer maxDurationMonths;

    @Column(name = "active")
    private Boolean active;

    @Version
    @Column(name = "version")
    private Long version;

    public CreditRateEntity() {
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

    public String getCspCode() {
        return cspCode;
    }

    public void setCspCode(String cspCode) {
        this.cspCode = cspCode;
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

    public Integer getMinDurationMonths() {
        return minDurationMonths;
    }

    public void setMinDurationMonths(Integer minDurationMonths) {
        this.minDurationMonths = minDurationMonths;
    }

    public Integer getMaxDurationMonths() {
        return maxDurationMonths;
    }

    public void setMaxDurationMonths(Integer maxDurationMonths) {
        this.maxDurationMonths = maxDurationMonths;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void merge(CreditRateEntity other) {
        MergeSupport.updateIfDifferent(this::setRateMin, this.rateMin, other.getRateMin());
        MergeSupport.updateIfDifferent(this::setRateMax, this.rateMax, other.getRateMax());
        MergeSupport.updateIfDifferent(this::setRateFormula, this.rateFormula, other.getRateFormula());
        MergeSupport.updateIfDifferent(this::setMinDurationMonths, this.minDurationMonths,
            other.getMinDurationMonths());
        MergeSupport.updateIfDifferent(this::setMaxDurationMonths, this.maxDurationMonths,
            other.getMaxDurationMonths());
        MergeSupport.updateIfDifferent(this::setActive, this.active, other.getActive());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreditRateEntity other)) {
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
            "CreditRateEntity [id=%s, cspCode=%s, rateMin=%s, rateMax=%s, rateFormula=%s, "
                + "minDurationMonths=%s, maxDurationMonths=%s, active=%s]",
            id, cspCode, rateMin, rateMax, rateFormula, minDurationMonths, maxDurationMonths, active);
    }
}
