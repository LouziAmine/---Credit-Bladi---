package com.credit.bladi.datasource.servicing.entity;

import com.credit.bladi.model.enums.InstallmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "installment")
public class InstallmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "credit_file_id", nullable = false)
    private Long creditFileId;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InstallmentStatus status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "last_reminder_sent_at")
    private Instant lastReminderSentAt;

    @Column(name = "declared_at")
    private Instant declaredAt;

    @Column(name = "declared_by", length = 100)
    private String declaredBy;

    @Column(name = "declared_source_rib", length = 34)
    private String declaredSourceRib;

    @Column(name = "late_penalty", precision = 19, scale = 2)
    private BigDecimal latePenalty;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public InstallmentEntity() {
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

    public Long getCreditFileId() {
        return creditFileId;
    }

    public void setCreditFileId(Long creditFileId) {
        this.creditFileId = creditFileId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(InstallmentStatus status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getLastReminderSentAt() {
        return lastReminderSentAt;
    }

    public Instant getDeclaredAt() {
        return declaredAt;
    }

    public String getDeclaredBy() {
        return declaredBy;
    }

    public String getDeclaredSourceRib() {
        return declaredSourceRib;
    }

    public BigDecimal getLatePenalty() {
        return latePenalty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstallmentEntity other)) {
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
            "InstallmentEntity [id=%s, creditFileId=%s, sequenceNumber=%s, dueDate=%s, status=%s]",
            id, creditFileId, sequenceNumber, dueDate, status);
    }
}
