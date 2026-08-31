package com.credit.bladi.service.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;

public interface IAuditService {
    void log(String username, AuditAction action, String details);

    void log(String username, AuditAction action, AuditTarget target, String details);

    CreditPage<AuditLogModel> getAuditLog(int page, int size);

    CreditPage<AuditLogModel> getAuditLog(AuditTarget target, int page, int size);
}
