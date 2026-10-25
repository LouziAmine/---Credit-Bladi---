package com.credit.bladi.unittest.service.verification;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ICE;
import static com.credit.bladi.unittest.mock.data.MockCodes.ID_001234567000089;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_PETIT_COMMERCE_SARL;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_09_06T10_00_00Z;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_CHARGEE_AFFAIRES1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.CentraleRisquesStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ScipStatus;
import com.credit.bladi.model.enums.SimulatedRecommendation;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ICurrentActorProvider;
import com.credit.bladi.service.verification.BamVerificationService;
import com.credit.bladi.service.verification.bam.IBamVerificationGateway;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BamVerificationServiceTest {
    @Mock
    private IBamVerificationGateway bamVerificationGateway;

    @Mock
    private IAuditService auditService;

    @Mock
    private ICurrentActorProvider currentActorProvider;

    private BamVerificationService service;

    private static final BamVerificationResult CLEAR_RESULT = new BamVerificationResult(
        ID_001234567000089, CODE_ICE, ScipStatus.CLEAR, List.of(), CentraleRisquesStatus.CLEAR, List.of(),
        SimulatedRecommendation.FAVORABLE, BamScenario.CLEAR, Instant.parse(DATE_2026_09_06T10_00_00Z));

    @BeforeEach
    void setUp() {
        service = new BamVerificationService(bamVerificationGateway, auditService, currentActorProvider);
    }

    @Test
    void verify_delegatesToTheGatewayAndReturnsItsResult() {
        when(bamVerificationGateway.verify(EnterpriseType.PME, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL, null))
            .thenReturn(CLEAR_RESULT);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_CHARGEE_AFFAIRES1);

        BamVerificationResult result = service.verify(EnterpriseType.PME, ID_001234567000089,
            COMPANY_ATLAS_TEXTILE_SARL,
            null);

        assertEquals(CLEAR_RESULT, result);
    }

    @Test
    void verify_logsAnAuditEntryWithTheResolvedScenarioAndRecommendation() {
        when(bamVerificationGateway.verify(EnterpriseType.PME, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL, null))
            .thenReturn(CLEAR_RESULT);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_CHARGEE_AFFAIRES1);

        service.verify(EnterpriseType.PME, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL, null);

        verify(auditService).log(eq(USER_CHARGEE_AFFAIRES1), eq(AuditAction.BAM_VERIFICATION_MOCK_RUN),
            eq("enterpriseType=PME, companyName=Atlas Textile SARL, scenario=CLEAR, recommendation=FAVORABLE"));
    }

    @Test
    void verify_passesTheForcedScenarioThroughToTheGateway() {
        when(bamVerificationGateway.verify(EnterpriseType.TPE, ID_001234567000089, COMPANY_PETIT_COMMERCE_SARL,
            BamScenario.ACTIVE_INCIDENT)).thenReturn(CLEAR_RESULT);
        when(currentActorProvider.getCurrentUsername()).thenReturn(USER_CHARGEE_AFFAIRES1);

        service.verify(EnterpriseType.TPE, ID_001234567000089, COMPANY_PETIT_COMMERCE_SARL,
            BamScenario.ACTIVE_INCIDENT);

        verify(bamVerificationGateway).verify(EnterpriseType.TPE, ID_001234567000089, COMPANY_PETIT_COMMERCE_SARL,
            BamScenario.ACTIVE_INCIDENT);
    }
}
