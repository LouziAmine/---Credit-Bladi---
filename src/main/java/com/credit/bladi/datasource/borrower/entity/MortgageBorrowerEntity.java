package com.credit.bladi.datasource.borrower.entity;

import com.credit.bladi.model.enums.BorrowerLinkOrigin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "mortgage_borrower")
public class MortgageBorrowerEntity {
    @Id
    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false, length = 40)
    private BorrowerLinkOrigin origin;

    @Column(name = "linked_by", length = 100)
    private String linkedBy;

    @Column(name = "linked_at", nullable = false)
    private Instant linkedAt;

    public MortgageBorrowerEntity() {
    }

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BorrowerLinkOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(BorrowerLinkOrigin origin) {
        this.origin = origin;
    }

    public String getLinkedBy() {
        return linkedBy;
    }

    public void setLinkedBy(String linkedBy) {
        this.linkedBy = linkedBy;
    }

    public Instant getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(Instant linkedAt) {
        this.linkedAt = linkedAt;
    }
}
