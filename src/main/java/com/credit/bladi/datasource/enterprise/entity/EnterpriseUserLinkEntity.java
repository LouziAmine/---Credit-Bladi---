package com.credit.bladi.datasource.enterprise.entity;

import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "enterprise_user_link")
public class EnterpriseUserLinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "enterprise_id", nullable = false)
    private Long enterpriseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private EnterpriseLinkRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnterpriseLinkStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false, length = 40)
    private EnterpriseLinkOrigin origin;

    @Column(name = "claimed_registration_number", length = 50)
    private String claimedRegistrationNumber;

    @Column(name = "requested_by_username", nullable = false, length = 100)
    private String requestedByUsername;

    @Column(name = "decided_by_username", length = 100)
    private String decidedByUsername;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public EnterpriseUserLinkEntity() {
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

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EnterpriseLinkRole getRole() {
        return role;
    }

    public void setRole(EnterpriseLinkRole role) {
        this.role = role;
    }

    public EnterpriseLinkStatus getStatus() {
        return status;
    }

    public void setStatus(EnterpriseLinkStatus status) {
        this.status = status;
    }

    public EnterpriseLinkOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(EnterpriseLinkOrigin origin) {
        this.origin = origin;
    }

    public String getClaimedRegistrationNumber() {
        return claimedRegistrationNumber;
    }

    public void setClaimedRegistrationNumber(String claimedRegistrationNumber) {
        this.claimedRegistrationNumber = claimedRegistrationNumber;
    }

    public String getRequestedByUsername() {
        return requestedByUsername;
    }

    public void setRequestedByUsername(String requestedByUsername) {
        this.requestedByUsername = requestedByUsername;
    }

    public String getDecidedByUsername() {
        return decidedByUsername;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnterpriseUserLinkEntity other)) {
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
            "EnterpriseUserLinkEntity [id=%s, enterpriseId=%s, userId=%s, role=%s, status=%s, origin=%s]",
            id, enterpriseId, userId, role, status, origin);
    }
}
