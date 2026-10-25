package com.credit.bladi.model.audit;

import com.credit.bladi.model.enums.AuditEntityType;
import java.util.Objects;
import java.util.UUID;

public record AuditTarget(AuditEntityType type, UUID publicId) {
    public AuditTarget {
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(publicId, "publicId cannot be null");
    }

    public static AuditTarget demand(UUID demandPublicId) {
        return new AuditTarget(AuditEntityType.DEMAND, demandPublicId);
    }
}
