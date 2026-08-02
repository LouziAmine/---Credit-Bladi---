package com.credit.bladi.service.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;

public interface IAuditService {

    void log(String username, AuditAction action, String details);

    CreditPage<AuditLogModel> getAuditLog(int page, int size);
}