package com.credit.bladi.datasource.audit.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.AuditEntityType;
import java.util.List;

public final class AuditLogEntityMapper {
    private AuditLogEntityMapper() {}

    public static AuditLogEntity mapToEntity(AuditLogModel auditLogModel) {
        AuditLogEntity auditLogEntity = new AuditLogEntity();
        auditLogEntity.setUsername(auditLogModel.getUsername());
        auditLogEntity.setAction(auditLogModel.getAction().name());
        auditLogEntity.setIpAddress(auditLogModel.getIpAddress());
        auditLogEntity.setDetails(auditLogModel.getDetails());
        auditLogEntity.setPerformedAt(auditLogModel.getPerformedAt());
        if (auditLogModel.getTarget() != null) {
            auditLogEntity.setEntityType(auditLogModel.getTarget().type().name());
            auditLogEntity.setEntityPublicId(auditLogModel.getTarget().publicId());
        }
        return auditLogEntity;
    }

    public static AuditLogModel mapFromEntity(AuditLogEntity auditLogEntity) {
        return AuditLogModel.getBuilder()
            .id(auditLogEntity.getId())
            .publicId(auditLogEntity.getPublicId())
            .username(auditLogEntity.getUsername())
            .action(AuditAction.valueOf(auditLogEntity.getAction()))
            .ipAddress(auditLogEntity.getIpAddress())
            .details(auditLogEntity.getDetails())
            .performedAt(auditLogEntity.getPerformedAt())
            .target(auditLogEntity.getEntityType() != null && auditLogEntity.getEntityPublicId() != null
                ? new AuditTarget(AuditEntityType.valueOf(auditLogEntity.getEntityType()),
                    auditLogEntity.getEntityPublicId())
                : null)
            .build();
    }

    public static CreditPage<AuditLogModel> mapToPage(
            List<AuditLogEntity> entities, long total, int page, int size) {
        return CreditPage.of(entities.stream().map(AuditLogEntityMapper::mapFromEntity).toList(), page, size, total);
    }
}
