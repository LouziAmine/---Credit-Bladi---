package com.credit.bladi.repository.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;

public interface IAuditLogRepository {

    void save(AuditLogModel entry);

    CreditPage<AuditLogModel> findAll(int page, int size);
}