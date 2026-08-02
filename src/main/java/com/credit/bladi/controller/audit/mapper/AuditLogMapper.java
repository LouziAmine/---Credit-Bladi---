package com.credit.bladi.controller.audit.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.audit.response.AuditLogResponseDTO;
import com.credit.bladi.model.audit.AuditLogModel;

public final class AuditLogMapper {

    private AuditLogMapper() {
    }

    private static AuditLogResponseDTO toDto(AuditLogModel auditLogModel) {
        if (auditLogModel == null) {
            return null;
        }
        AuditLogResponseDTO auditLogResponseDTO = new AuditLogResponseDTO();
        auditLogResponseDTO.setId(auditLogModel.getId());
        auditLogResponseDTO.setUsername(auditLogModel.getUsername());
        auditLogResponseDTO.setAction(auditLogModel.getAction() != null ? auditLogModel.getAction().name() : null);
        auditLogResponseDTO.setIpAddress(auditLogModel.getIpAddress());
        auditLogResponseDTO.setDetails(auditLogModel.getDetails());
        auditLogResponseDTO.setPerformedAt(auditLogModel.getPerformedAt());
        return auditLogResponseDTO;
    }

    public static CreditPage<AuditLogResponseDTO> toCreditPage(CreditPage<AuditLogModel> source) {
        return CreditPageMapper.map(source, AuditLogMapper::toDto);
    }
}
