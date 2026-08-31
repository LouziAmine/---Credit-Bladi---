package com.credit.bladi.service.audit;

import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.repository.audit.IAuditLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuditLogWriter {
    private final IAuditLogRepository auditLogRepository;

    public AuditLogWriter(IAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void writeWithOperation(AuditLogModel entry) {
        auditLogRepository.save(entry);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeIndependently(AuditLogModel entry) {
        auditLogRepository.save(entry);
    }
}
