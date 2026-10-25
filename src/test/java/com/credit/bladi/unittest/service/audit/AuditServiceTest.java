package com.credit.bladi.unittest.service.audit;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FAILURE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SUCCESS;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_10_0_0_1;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DB_UNAVAILABLE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.audit.AuditLogModel;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.repository.audit.IAuditLogRepository;
import com.credit.bladi.service.audit.AuditLogWriter;
import com.credit.bladi.service.audit.AuditService;
import com.credit.bladi.service.request.IRequestContext;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Clock;
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
        auditService = new AuditService(auditLogRepository, new AuditLogWriter(auditLogRepository), requestContext,
            meterRegistry, Clock.systemUTC());
    }

    @Test
    void shouldBuildAndSaveAuditLogEntryWithClientIpAndTimestamp() {
        when(requestContext.getClientIp()).thenReturn(IP_10_0_0_1);

        auditService.log(USER_JDOE, AuditAction.LOGIN, "SUCCESS role=CLIENT");

        ArgumentCaptor<AuditLogModel> captor = ArgumentCaptor.forClass(AuditLogModel.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLogModel entry = captor.getValue();
        assertEquals(USER_JDOE, entry.getUsername());
        assertEquals(AuditAction.LOGIN, entry.getAction());
        assertEquals(IP_10_0_0_1, entry.getIpAddress());
        assertEquals("SUCCESS role=CLIENT", entry.getDetails());
        assertNotNull(entry.getPerformedAt());
    }

    @Test
    void shouldSaveEntryWithNullUsernameWithoutThrowing() {
        when(requestContext.getClientIp()).thenReturn(IP_10_0_0_1);

        assertDoesNotThrow(() -> auditService.log(null, AuditAction.LOGIN, CODE_FAILURE));

        ArgumentCaptor<AuditLogModel> captor = ArgumentCaptor.forClass(AuditLogModel.class);
        verify(auditLogRepository).save(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals(null, captor.getValue().getUsername());
    }

    @Test
    void shouldNotIncrementFailureCounterOnSuccess() {
        when(requestContext.getClientIp()).thenReturn(IP_10_0_0_1);

        auditService.log(USER_JDOE, AuditAction.SIMULATION, null);

        assertEquals(0.0, counterValue(AuditAction.SIMULATION));
    }

    @Test
    void shouldSwallowRepositoryFailureOfOptionalAuditAndIncrementFailureCounter() {
        when(requestContext.getClientIp()).thenReturn(IP_10_0_0_1);
        doThrow(new RuntimeException(LABEL_DB_UNAVAILABLE)).when(auditLogRepository).save(any());

        assertDoesNotThrow(() -> auditService.log(USER_JDOE, AuditAction.LOGIN, CODE_SUCCESS));

        assertEquals(1.0, counterValue(AuditAction.LOGIN));
    }

    @Test
    void shouldAbortOperationWhenMandatoryAuditCannotBeWritten() {
        when(requestContext.getClientIp()).thenReturn(IP_10_0_0_1);
        doThrow(new RuntimeException(LABEL_DB_UNAVAILABLE)).when(auditLogRepository).save(any());

        assertThrows(IllegalStateException.class,
            () -> auditService.log(USER_JDOE, AuditAction.BAM_PARAMETERS_UPDATE, "id=1"));

        assertEquals(1.0, counterValue(AuditAction.BAM_PARAMETERS_UPDATE));
    }

    @Test
    void shouldTagFailureCounterByActionIndependently() {
        when(requestContext.getClientIp()).thenReturn(IP_10_0_0_1);
        doThrow(new RuntimeException(LABEL_DB_UNAVAILABLE)).when(auditLogRepository).save(any());

        auditService.log(USER_JDOE, AuditAction.SIMULATION_REJECTED, "reason=DEBT_RATIO_EXCEEDED");
        auditService.log(USER_JDOE, AuditAction.SIMULATION_REJECTED, "reason=DURATION_LIMIT_EXCEEDED");

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
