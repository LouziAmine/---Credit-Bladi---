package com.credit.bladi.controller.dto.audit.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Schema(
    name = "AuditLogResponseDTO",
    description = "A single audit trail entry — tracks who did what, when, and from where."
)
public class AuditLogResponseDTO {

    @Schema(description = "Entry identifier.", example = "42")
    private Long id;

    @Schema(description = "Username of the actor, or null for unauthenticated actions (e.g. a public "
        + "simulation).", example = "jdoe")
    private String username;

    @Schema(description = "Action performed.", example = "SIMULATION_REJECTED")
    private String action;

    @Schema(description = "IP address the action was performed from.", example = "10.0.0.1")
    private String ipAddress;

    @Schema(description = "Free-text details about the action (e.g. rejection reason, updated resource id).",
        example = "reason=DEBT_RATIO_EXCEEDED, cspCode=FONC, amount=500000.00 MAD, duration=240")
    private String details;

    @Schema(description = "Timestamp the action was performed at.", example = "2026-07-08 10:00:00")
    private LocalDateTime performedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AuditLogResponseDTO && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(
            "AuditLogResponseDTO [id=%s, username=%s, action=%s, ipAddress=%s, performedAt=%s]",
            id, username, action, ipAddress, performedAt);
    }
}
