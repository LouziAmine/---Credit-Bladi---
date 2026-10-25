package com.credit.bladi.datasource.audit;

import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogDatasource extends JpaRepository<AuditLogEntity, Long> {
    Page<AuditLogEntity> findByEntityTypeAndEntityPublicId(String entityType, UUID entityPublicId, Pageable pageable);
}
