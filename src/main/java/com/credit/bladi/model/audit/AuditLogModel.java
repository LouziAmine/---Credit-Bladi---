package com.credit.bladi.model.audit;

import com.credit.bladi.model.enums.AuditAction;
import java.time.LocalDateTime;

public class AuditLogModel {

    private Long id;

    private String username;

    private AuditAction action;

    private String ipAddress;

    private String details;

    private LocalDateTime performedAt;

    private AuditLogModel() {
    }

    public Long getId() {
        return id;
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

    private void setId(Long id) {
        this.id = id;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setAction(AuditAction action) {
        this.action = action;
    }

    private void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    private void setDetails(String details) {
        this.details = details;
    }

    private void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;

        private String username;

        private AuditAction action;

        private String ipAddress;

        private String details;

        private LocalDateTime performedAt;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
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

        public AuditLogModel build() {
            AuditLogModel model = new AuditLogModel();
            model.setId(id);
            model.setUsername(username);
            model.setAction(action);
            model.setIpAddress(ipAddress);
            model.setDetails(details);
            model.setPerformedAt(performedAt);
            return model;
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