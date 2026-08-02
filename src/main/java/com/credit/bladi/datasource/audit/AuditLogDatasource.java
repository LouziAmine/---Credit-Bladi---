package com.credit.bladi.datasource.audit;

import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogDatasource extends JpaRepository<AuditLogEntity, Long> {
}