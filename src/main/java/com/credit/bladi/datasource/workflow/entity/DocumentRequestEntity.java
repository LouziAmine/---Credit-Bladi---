package com.credit.bladi.datasource.workflow.entity;

import com.credit.bladi.model.enums.DocumentRequestStatus;
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
@Table(name = "document_request")
public class DocumentRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Column(name = "demand_id", nullable = false)
    private Long demandId;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DocumentRequestStatus status;

    @Column(name = "requested_by_username", nullable = false, length = 100)
    private String requestedByUsername;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "fulfilled_document_id")
    private Long fulfilledDocumentId;

    @Column(name = "fulfilled_at")
    private Instant fulfilledAt;

    public DocumentRequestEntity() {
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

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public DocumentRequestStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentRequestStatus status) {
        this.status = status;
    }

    public String getRequestedByUsername() {
        return requestedByUsername;
    }

    public void setRequestedByUsername(String requestedByUsername) {
        this.requestedByUsername = requestedByUsername;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getFulfilledDocumentId() {
        return fulfilledDocumentId;
    }

    public Instant getFulfilledAt() {
        return fulfilledAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentRequestEntity other)) {
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
        return String.format("DocumentRequestEntity [id=%s, demandId=%s, documentType=%s, status=%s]",
            id, demandId, documentType, status);
    }
}
