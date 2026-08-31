package com.credit.bladi.model.audit;

import com.credit.bladi.model.enums.AuditAction;
import java.time.LocalDateTime;
import java.util.UUID;

public final class AuditLogModel {
    private final Long id;

    private final UUID publicId;

    private final String username;

    private final AuditAction action;

    private final String ipAddress;

    private final String details;

    private final LocalDateTime performedAt;

    private final AuditTarget target;

    private AuditLogModel(Builder builder) {
        this.id = builder.id;
        this.publicId = builder.publicId;
        this.username = builder.username;
        this.action = builder.action;
        this.ipAddress = builder.ipAddress;
        this.details = builder.details;
        this.performedAt = builder.performedAt;
        this.target = builder.target;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getUsername() {
        return username;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public AuditTarget getTarget() {
        return target;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;

        private UUID publicId;

        private String username;

        private AuditAction action;

        private String ipAddress;

        private String details;

        private LocalDateTime performedAt;

        private AuditTarget target;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder publicId(UUID publicId) {
            this.publicId = publicId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder action(AuditAction action) {
            this.action = action;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder details(String details) {
            this.details = details;
            return this;
        }

        public Builder performedAt(LocalDateTime performedAt) {
            this.performedAt = performedAt;
            return this;
        }

        public Builder target(AuditTarget target) {
            this.target = target;
            return this;
        }

        public AuditLogModel build() {
            return new AuditLogModel(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditLogModel other)) {
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
            "AuditLogModel [id=%s, username=%s, action=%s, ipAddress=%s, details=%s, performedAt=%s]",
            id, username, action, ipAddress, details, performedAt);
    }
}
