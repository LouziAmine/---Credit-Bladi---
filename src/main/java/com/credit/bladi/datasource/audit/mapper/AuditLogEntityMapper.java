package com.credit.bladi.datasource.audit.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
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
        return auditLogEntity;
    }

    public static AuditLogModel mapFromEntity(AuditLogEntity auditLogEntity) {
        return AuditLogModel.getBuilder()
            .id(auditLogEntity.getId())
            .username(auditLogEntity.getUsername())
            .action(AuditAction.valueOf(auditLogEntity.getAction()))
            .ipAddress(auditLogEntity.getIpAddress())
            .details(auditLogEntity.getDetails())
            .performedAt(auditLogEntity.getPerformedAt())
            .build();
    }

    public static CreditPage<AuditLogModel> mapToPage(
            List<AuditLogEntity> entities, long total, int page, int size) {
        CreditPage<AuditLogModel> result = new CreditPage<>();
        result.setItems(entities.stream().map(AuditLogEntityMapper::mapFromEntity).toList());
        result.setCurrentPage(page);
        result.setLastPage(size > 0 ? (int) Math.ceil((double) total / size) : 0);
        result.setSize(size);
        result.setTotal((int) total);
        return result;
    }
}