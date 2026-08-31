package com.credit.bladi.repository.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.audit.AuditLogDatasource;
import com.credit.bladi.datasource.audit.entity.AuditLogEntity;
import com.credit.bladi.datasource.audit.mapper.AuditLogEntityMapper;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.audit.AuditTarget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogRepository implements IAuditLogRepository {
    private static final Logger LOGGER = LogManager.getLogger(AuditLogRepository.class);

    private final AuditLogDatasource datasource;

    public AuditLogRepository(AuditLogDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public void save(AuditLogModel entry) {
        LOGGER.debug("save - action={}, username={}", entry.getAction(), entry.getUsername());
        datasource.save(AuditLogEntityMapper.mapToEntity(entry));
    }

    @Override
    public CreditPage<AuditLogModel> findAll(int page, int size) {
        LOGGER.debug("findAll - page: {}, size: {}", page, size);
        Page<AuditLogEntity> result = datasource.findAll(
            PageRequest.of(page - 1, size, Sort.by("performedAt").descending()));
        return AuditLogEntityMapper.mapToPage(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    public CreditPage<AuditLogModel> findByTarget(AuditTarget target, int page, int size) {
        Page<AuditLogEntity> result = datasource.findByEntityTypeAndEntityPublicId(target.type().name(),
            target.publicId(), PageRequest.of(page - 1, size, Sort.by("performedAt").descending()));
        return AuditLogEntityMapper.mapToPage(result.getContent(), result.getTotalElements(), page, size);
    }
}
