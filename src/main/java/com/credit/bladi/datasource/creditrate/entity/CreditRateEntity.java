package com.credit.bladi.datasource.creditrate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Consumer;

@Entity
@Table(name = "credit_rate")
public class CreditRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "csp_code", nullable = false, length = 10)
    private String cspCode;

    @Column(name = "rate_min")
    private BigDecimal rateMin;

    @Column(name = "rate_max")
    private BigDecimal rateMax;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void merge(CreditRateEntity other) {
        updateIfDifferent(this::setRateMin, this.rateMin, other.getRateMin());
        updateIfDifferent(this::setRateMax, this.rateMax, other.getRateMax());
        updateIfDifferent(this::setMinDurationMonths, this.minDurationMonths, other.getMinDurationMonths());
        updateIfDifferent(this::setMaxDurationMonths, this.maxDurationMonths, other.getMaxDurationMonths());
        updateIfDifferent(this::setActive, this.active, other.getActive());
    }

    private static <T> void updateIfDifferent(Consumer<T> setter, T current, T incoming) {
        if (Objects.nonNull(incoming) && !valuesEqual(current, incoming)) {
            setter.accept(incoming);
        }
    }

    private static boolean valuesEqual(Object current, Object incoming) {
        if (current instanceof BigDecimal currentValue && incoming instanceof BigDecimal incomingValue) {
            return currentValue.compareTo(incomingValue) == 0;
        }
        return Objects.equals(current, incoming);
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
            "CreditRateEntity [id=%s, cspCode=%s, rateMin=%s, rateMax=%s, "
                + "minDurationMonths=%s, maxDurationMonths=%s, active=%s]",
            id, cspCode, rateMin, rateMax, minDurationMonths, maxDurationMonths, active);
    }
}