package com.credit.bladi.datasource.mourabaha.entity;

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
@Table(name = "mourabaha_parameters")
public class MourabahaParametersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "profit_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal profitRate;

    @Column(name = "vat_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal vatRate;

    @Column(name = "takaful_monthly_rate", nullable = false, precision = 6, scale = 4)
    private BigDecimal takafulMonthlyRate;

    @Column(name = "active")
    private Boolean active;

    @Version
    @Column(name = "version")
    private Long version;

    public MourabahaParametersEntity() {
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

    public BigDecimal getTakafulMonthlyRate() {
        return takafulMonthlyRate;
    }

    public void setTakafulMonthlyRate(BigDecimal takafulMonthlyRate) {
        this.takafulMonthlyRate = takafulMonthlyRate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void merge(MourabahaParametersEntity other) {
        MergeSupport.updateIfDifferent(this::setProfitRate, this.profitRate, other.getProfitRate());
        MergeSupport.updateIfDifferent(this::setVatRate, this.vatRate, other.getVatRate());
        MergeSupport.updateIfDifferent(this::setTakafulMonthlyRate, this.takafulMonthlyRate,
            other.getTakafulMonthlyRate());
        MergeSupport.updateIfDifferent(this::setActive, this.active, other.getActive());
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
            "MourabahaParametersEntity [id=%s, profitRate=%s, vatRate=%s, takafulMonthlyRate=%s, active=%s]",
            id, profitRate, vatRate, takafulMonthlyRate, active);
    }
}
