package com.credit.bladi.controller.audit.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.common.EnumParsers;
import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditEntityType;
import java.util.UUID;

public final class AuditLogMapper {
    private AuditLogMapper() {
    }

    private static AuditLogResponseDTO toDto(AuditLogModel auditLogModel) {
        if (auditLogModel == null) {
            return null;
        }
        return new AuditLogResponseDTO(
            auditLogModel.getPublicId(),
            auditLogModel.getUsername(),
            auditLogModel.getAction() != null ? auditLogModel.getAction().name() : null,
            auditLogModel.getIpAddress(),
            auditLogModel.getDetails(),
            auditLogModel.getPerformedAt(),
            auditLogModel.getTarget() != null ? auditLogModel.getTarget().type().name() : null,
            auditLogModel.getTarget() != null ? auditLogModel.getTarget().publicId() : null);
    }

    public static AuditTarget toTarget(String entityType, UUID entityId) {
        if (entityType == null && entityId == null) {
            return null;
        }
        if (entityType == null || entityId == null) {
            throw ValidationException.requiredTogether("entityType", "entityId");
        }
        return new AuditTarget(EnumParsers.parse(AuditEntityType.class, entityType, "entityType"), entityId);
    }

    public static CreditPage<AuditLogResponseDTO> toCreditPage(CreditPage<AuditLogModel> source) {
        return CreditPageMapper.map(source, AuditLogMapper::toDto);
    }
}
