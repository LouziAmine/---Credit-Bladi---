package com.credit.bladi.datasource.servicing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "early_repayment")
public class EarlyRepaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "credit_file_id", nullable = false)
    private Long creditFileId;

    @Column(name = "full_repayment", nullable = false)
    private Boolean fullRepayment;

    @Column(name = "outstanding_before", nullable = false, precision = 19, scale = 2)
    private BigDecimal outstandingBefore;

    @Column(name = "repaid_principal", nullable = false, precision = 19, scale = 2)
    private BigDecimal repaidPrincipal;

    @Column(name = "accrued_interest", nullable = false, precision = 19, scale = 2)
    private BigDecimal accruedInterest;

    @Column(name = "indemnity", nullable = false, precision = 19, scale = 2)
    private BigDecimal indemnity;

    @Column(name = "total_paid", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPaid;

    @Column(name = "outstanding_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal outstandingAfter;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(name = "performed_at", nullable = false)
    private Instant performedAt;

    @PrePersist
    private void generatePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public Long getCreditFileId() {
        return creditFileId;
    }

    public void setCreditFileId(Long creditFileId) {
        this.creditFileId = creditFileId;
    }

    public Boolean getFullRepayment() {
        return fullRepayment;
    }

    public void setFullRepayment(Boolean fullRepayment) {
        this.fullRepayment = fullRepayment;
    }

    public BigDecimal getOutstandingBefore() {
        return outstandingBefore;
    }

    public void setOutstandingBefore(BigDecimal outstandingBefore) {
        this.outstandingBefore = outstandingBefore;
    }

    public BigDecimal getRepaidPrincipal() {
        return repaidPrincipal;
    }

    public void setRepaidPrincipal(BigDecimal repaidPrincipal) {
        this.repaidPrincipal = repaidPrincipal;
    }

    public BigDecimal getAccruedInterest() {
        return accruedInterest;
    }

    public void setAccruedInterest(BigDecimal accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    public BigDecimal getIndemnity() {
        return indemnity;
    }

    public void setIndemnity(BigDecimal indemnity) {
        this.indemnity = indemnity;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getOutstandingAfter() {
        return outstandingAfter;
    }

    public void setOutstandingAfter(BigDecimal outstandingAfter) {
        this.outstandingAfter = outstandingAfter;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public Instant getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Instant performedAt) {
        this.performedAt = performedAt;
    }
}
