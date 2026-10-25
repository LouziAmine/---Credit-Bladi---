package com.credit.bladi.service.audit;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.audit.AuditTarget;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.repository.audit.IAuditLogRepository;
import com.credit.bladi.service.request.IRequestContext;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService implements IAuditService {
    private static final Logger LOGGER = LogManager.getLogger(AuditService.class);

    private static final int MAX_DETAILS_LENGTH = 500;

    private static final String TRUNCATION_MARKER = "...";

    private final IAuditLogRepository auditLogRepository;

    private final AuditLogWriter writer;

    private final IRequestContext requestContext;

    private final MeterRegistry meterRegistry;

    private final Clock clock;

    public AuditService(IAuditLogRepository auditLogRepository, AuditLogWriter writer,
                         IRequestContext requestContext, MeterRegistry meterRegistry, Clock clock) {
        this.auditLogRepository = auditLogRepository;
        this.writer = writer;
        this.requestContext = requestContext;
        this.meterRegistry = meterRegistry;
        this.clock = clock;
    }

    @Override
    public void log(String username, AuditAction action, String details) {
        log(username, action, null, details);
    }

    @Override
    public void log(String username, AuditAction action, AuditTarget target, String details) {
        AuditLogModel entry = AuditLogModel.getBuilder()
            .username(username)
            .action(action)
            .ipAddress(requestContext.getClientIp())
            .target(target)
            .details(truncate(details))
            .performedAt(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC))
            .build();
        if (action.isMandatory()) {
            try {
                writer.writeWithOperation(entry);
            } catch (RuntimeException e) {
                recordFailure(action, username, e);
                throw new IllegalStateException("Audit trail unavailable for " + action + ", operation aborted", e);
            }
            return;
        }
        try {
            writer.writeIndependently(entry);
        } catch (RuntimeException e) {
            recordFailure(action, username, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<AuditLogModel> getAuditLog(int page, int size) {
        LOGGER.debug("getAuditLog - page={}, size={}", page, size);
        return auditLogRepository.findAll(page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<AuditLogModel> getAuditLog(AuditTarget target, int page, int size) {
        return auditLogRepository.findByTarget(target, page, size);
    }

    private void recordFailure(AuditAction action, String username, RuntimeException e) {
        LOGGER.error("AUDIT_WRITE_FAILURE action={} username={}", action, username, e);
        Counter.builder("audit.log.failures")
            .tag("action", action.name())
            .register(meterRegistry)
            .increment();
    }

    private static String truncate(String details) {
        if (details == null || details.length() <= MAX_DETAILS_LENGTH) {
            return details;
        }
        return details.substring(0, MAX_DETAILS_LENGTH - TRUNCATION_MARKER.length()) + TRUNCATION_MARKER;
    }
}
