package com.credit.bladi.service.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.repository.audit.IAuditLogRepository;
import com.credit.bladi.service.request.IRequestContext;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService implements IAuditService {

    private static final Logger LOGGER = LogManager.getLogger(AuditService.class);

    private final IAuditLogRepository auditLogRepository;
    private final IRequestContext requestContext;
    private final MeterRegistry meterRegistry;

    public AuditService(IAuditLogRepository auditLogRepository,
                         IRequestContext requestContext,
                         MeterRegistry meterRegistry) {
        this.auditLogRepository = auditLogRepository;
        this.requestContext = requestContext;
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String username, AuditAction action, String details) {
        try {
            AuditLogModel entry = AuditLogModel.getBuilder()
                .username(username)
                .action(action)
                .ipAddress(requestContext.getClientIp())
                .details(details)
                .performedAt(LocalDateTime.now())
                .build();
            auditLogRepository.save(entry);
        } catch (Exception e) {
            LOGGER.error("AUDIT_WRITE_FAILURE action={} username={}", action, username, e);
            Counter.builder("audit.log.failures")
                .tag("action", action.name())
                .register(meterRegistry)
                .increment();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<AuditLogModel> getAuditLog(int page, int size) {
        LOGGER.debug("getAuditLog - page={}, size={}", page, size);
        return auditLogRepository.findAll(page, size);
    }
}