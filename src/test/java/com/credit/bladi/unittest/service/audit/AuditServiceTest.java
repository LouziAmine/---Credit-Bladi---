package com.credit.bladi.unittest.service.audit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.repository.audit.IAuditLogRepository;
import com.credit.bladi.service.audit.AuditService;
import com.credit.bladi.service.request.IRequestContext;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class AuditServiceTest {

    private IAuditLogRepository auditLogRepository;
    private IRequestContext requestContext;
    private SimpleMeterRegistry meterRegistry;
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditLogRepository = mock(IAuditLogRepository.class);
        requestContext = mock(IRequestContext.class);
        meterRegistry = new SimpleMeterRegistry();
        auditService = new AuditService(auditLogRepository, requestContext, meterRegistry);
    }

    @Test
    void shouldBuildAndSaveAuditLogEntryWithClientIpAndTimestamp() {
        when(requestContext.getClientIp()).thenReturn("10.0.0.1");

        auditService.log("jdoe", AuditAction.LOGIN, "SUCCESS role=CLIENT");

        ArgumentCaptor<AuditLogModel> captor = ArgumentCaptor.forClass(AuditLogModel.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLogModel entry = captor.getValue();
        assertEquals("jdoe", entry.getUsername());
        assertEquals(AuditAction.LOGIN, entry.getAction());
        assertEquals("10.0.0.1", entry.getIpAddress());
        assertEquals("SUCCESS role=CLIENT", entry.getDetails());
        assertNotNull(entry.getPerformedAt());
    }

    @Test
    void shouldSaveEntryWithNullUsernameWithoutThrowing() {
        when(requestContext.getClientIp()).thenReturn("10.0.0.1");

        assertDoesNotThrow(() -> auditService.log(null, AuditAction.LOGIN, "FAILURE"));

        ArgumentCaptor<AuditLogModel> captor = ArgumentCaptor.forClass(AuditLogModel.class);
        verify(auditLogRepository).save(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals(null, captor.getValue().getUsername());
    }

    @Test
    void shouldNotIncrementFailureCounterOnSuccess() {
        when(requestContext.getClientIp()).thenReturn("10.0.0.1");

        auditService.log("jdoe", AuditAction.SIMULATION, null);

        assertEquals(0.0, counterValue(AuditAction.SIMULATION));
    }

    @Test
    void shouldSwallowRepositoryFailureAndIncrementFailureCounter() {
        when(requestContext.getClientIp()).thenReturn("10.0.0.1");
        doThrow(new RuntimeException("DB unavailable")).when(auditLogRepository).save(any());

        assertDoesNotThrow(() -> auditService.log("jdoe", AuditAction.BAM_PARAMETERS_UPDATE, "id=1"));

        assertEquals(1.0, counterValue(AuditAction.BAM_PARAMETERS_UPDATE));
    }

    @Test
    void shouldTagFailureCounterByActionIndependently() {
        when(requestContext.getClientIp()).thenReturn("10.0.0.1");
        doThrow(new RuntimeException("DB unavailable")).when(auditLogRepository).save(any());

        auditService.log("jdoe", AuditAction.SIMULATION_REJECTED, "reason=DEBT_RATIO_EXCEEDED");
        auditService.log("jdoe", AuditAction.SIMULATION_REJECTED, "reason=DURATION_LIMIT_EXCEEDED");

        assertEquals(2.0, counterValue(AuditAction.SIMULATION_REJECTED));
        assertEquals(0.0, counterValue(AuditAction.LOGIN));
    }

    @Test
    void shouldDelegateGetAuditLogToRepository() {
        CreditPage<AuditLogModel> page = new CreditPage<>();
        when(auditLogRepository.findAll(1, 20)).thenReturn(page);

        CreditPage<AuditLogModel> result = auditService.getAuditLog(1, 20);

        assertEquals(page, result);
    }

    private double counterValue(AuditAction action) {
        Counter counter = meterRegistry.find("audit.log.failures").tag("action", action.name()).counter();
        return counter == null ? 0.0 : counter.count();
    }
}