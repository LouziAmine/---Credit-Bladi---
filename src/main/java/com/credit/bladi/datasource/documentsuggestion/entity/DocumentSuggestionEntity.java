package com.credit.bladi.datasource.documentsuggestion.entity;

import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.DocumentSuggestionScope;
import com.credit.bladi.model.enums.EnterpriseType;
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
@Table(name = "document_suggestion")
public class DocumentSuggestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_scope", nullable = false, length = 20)
    private DocumentSuggestionScope clientScope;

    @Enumerated(EnumType.STRING)
    @Column(name = "enterprise_type", length = 30)
    private EnterpriseType enterpriseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "csp_code", length = 10)
    private CspCode cspCode;

    @Column(name = "label", nullable = false, length = 200)
    private String label;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public DocumentSuggestionEntity() {
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

    public void setClientScope(DocumentSuggestionScope clientScope) {
        this.clientScope = clientScope;
    }

    public EnterpriseType getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(EnterpriseType enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public CspCode getCspCode() {
        return cspCode;
    }

    public void setCspCode(CspCode cspCode) {
        this.cspCode = cspCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
        if (!(o instanceof DocumentSuggestionEntity other)) {
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
            "DocumentSuggestionEntity [id=%s, clientScope=%s, enterpriseType=%s, cspCode=%s, label=%s]",
            id, clientScope, enterpriseType, cspCode, label);
    }
}
