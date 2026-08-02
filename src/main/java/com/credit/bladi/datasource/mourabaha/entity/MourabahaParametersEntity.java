package com.credit.bladi.datasource.mourabaha.entity;

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
@Table(name = "mourabaha_parameters")
public class MourabahaParametersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "profit_rate", nullable = false)
    private BigDecimal profitRate;

    @Column(name = "vat_rate", nullable = false)
    private BigDecimal vatRate;

    @Column(name = "active")
    private Boolean active;

    @Version
    @Column(name = "version")
    private Long version;

    public MourabahaParametersEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void merge(MourabahaParametersEntity other) {
        updateIfDifferent(this::setProfitRate, this.profitRate, other.getProfitRate());
        updateIfDifferent(this::setVatRate, this.vatRate, other.getVatRate());
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
        if (!(o instanceof MourabahaParametersEntity other)) {
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
            "MourabahaParametersEntity [id=%s, profitRate=%s, vatRate=%s, active=%s]",
            id, profitRate, vatRate, active);
    }
}
