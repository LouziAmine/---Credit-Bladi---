package com.credit.bladi.repository.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.audit.AuditTarget;

public interface IAuditLogRepository {
    void save(AuditLogModel entry);

    CreditPage<AuditLogModel> findAll(int page, int size);

    CreditPage<AuditLogModel> findByTarget(AuditTarget target, int page, int size);
}
