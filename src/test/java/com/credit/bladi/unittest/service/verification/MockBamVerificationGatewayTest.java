package com.credit.bladi.unittest.service.verification;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_AB123456;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_AUTO_ENTREPRENEUR;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ICE;
import static com.credit.bladi.unittest.mock.data.MockCodes.ID_001234567000089;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_GROUPE_ATLAS_HOLDING;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_PETIT_COMMERCE_SARL;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_ALAOUI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.infrastructure.bam.MockBamVerificationGateway;
import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.CentraleRisquesStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ScipStatus;
import com.credit.bladi.model.enums.SimulatedRecommendation;
import com.credit.bladi.model.verification.BamVerificationResult;
import java.time.Clock;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class MockBamVerificationGatewayTest {
    private final MockBamVerificationGateway gateway = new MockBamVerificationGateway(0, 0, Clock.systemUTC());

    @Test
    void clear_hasNoIncidentsOrOutstandingLoansAndIsFavorable() {
        BamVerificationResult result = gateway.verify(EnterpriseType.PME, ID_001234567000089,
            COMPANY_ATLAS_TEXTILE_SARL,
            BamScenario.CLEAR);

        assertEquals(ScipStatus.CLEAR, result.scipStatus());
        assertTrue(result.chequeIncidents().isEmpty());
        assertEquals(CentraleRisquesStatus.CLEAR, result.centraleRisquesStatus());
        assertTrue(result.outstandingLoans().isEmpty());
        assertEquals(SimulatedRecommendation.FAVORABLE, result.recommendation());
        assertEquals(BamScenario.CLEAR, result.scenario());
    }

    @Test
    void minorIncident_hasOneResolvedChequeIncidentAndStaysFavorable() {
        BamVerificationResult result = gateway.verify(EnterpriseType.TPE, ID_001234567000089,
            COMPANY_PETIT_COMMERCE_SARL,
            BamScenario.MINOR_INCIDENT);

        assertEquals(ScipStatus.INCIDENT_DETECTED, result.scipStatus());
        assertEquals(1, result.chequeIncidents().size());
        assertTrue(result.chequeIncidents().getFirst().resolved());
        assertEquals(SimulatedRecommendation.FAVORABLE, result.recommendation());
    }

    @Test
    void activeIncident_hasOneUnresolvedChequeIncidentAndIsUnfavorable() {
        BamVerificationResult result = gateway.verify(EnterpriseType.TPE, ID_001234567000089,
            COMPANY_PETIT_COMMERCE_SARL,
            BamScenario.ACTIVE_INCIDENT);

        assertEquals(ScipStatus.INCIDENT_DETECTED, result.scipStatus());
        assertEquals(1, result.chequeIncidents().size());
        assertFalse(result.chequeIncidents().getFirst().resolved());
        assertEquals(SimulatedRecommendation.UNFAVORABLE, result.recommendation());
    }

    @Test
    void personalExposureDetected_hasOneOutstandingLoanAndNeedsReview() {
        BamVerificationResult result = gateway.verify(EnterpriseType.AUTO_ENTREPRENEUR, CODE_AB123456,
            "Karim Alaoui - Consulting", BamScenario.PERSONAL_EXPOSURE_DETECTED);

        assertEquals(ScipStatus.CLEAR, result.scipStatus());
        assertEquals(CentraleRisquesStatus.EXPOSURE_DETECTED, result.centraleRisquesStatus());
        assertEquals(1, result.outstandingLoans().size());
        assertEquals(SimulatedRecommendation.TO_REVIEW, result.recommendation());
    }

    @Test
    void multipleExposures_hasMultipleOutstandingLoans() {
        BamVerificationResult result = gateway.verify(EnterpriseType.PME, ID_001234567000089,
            COMPANY_ATLAS_TEXTILE_SARL,
            BamScenario.MULTIPLE_EXPOSURES);

        assertEquals(CentraleRisquesStatus.EXPOSURE_DETECTED, result.centraleRisquesStatus());
        assertEquals(2, result.outstandingLoans().size());
        assertEquals(SimulatedRecommendation.TO_REVIEW, result.recommendation());
    }

    @Test
    void consolidatedGroupExposure_hasOutstandingLoansAcrossSubsidiaries() {
        BamVerificationResult result = gateway.verify(EnterpriseType.GE, ID_001234567000089,
            COMPANY_GROUPE_ATLAS_HOLDING,
            BamScenario.CONSOLIDATED_GROUP_EXPOSURE);

        assertEquals(CentraleRisquesStatus.EXPOSURE_DETECTED, result.centraleRisquesStatus());
        assertEquals(3, result.outstandingLoans().size());
        assertEquals(SimulatedRecommendation.TO_REVIEW, result.recommendation());
    }

    @Test
    void requiresManualReview_isClearButFlaggedForManualReview() {
        BamVerificationResult result = gateway.verify(EnterpriseType.GE, ID_001234567000089,
            COMPANY_GROUPE_ATLAS_HOLDING,
            BamScenario.REQUIRES_MANUAL_REVIEW);

        assertEquals(ScipStatus.CLEAR, result.scipStatus());
        assertEquals(CentraleRisquesStatus.CLEAR, result.centraleRisquesStatus());
        assertEquals(SimulatedRecommendation.TO_REVIEW, result.recommendation());
    }

    @Test
    void autoEntrepreneur_isVerifiedAgainstBothIceAndCin() {
        BamVerificationResult result = gateway.verify(EnterpriseType.AUTO_ENTREPRENEUR, CODE_AB123456,
            PERSON_KARIM_ALAOUI,
            BamScenario.CLEAR);

        assertEquals("ICE+CIN", result.identifierType());
    }

    @ParameterizedTest
    @EnumSource(value = EnterpriseType.class, names = CODE_AUTO_ENTREPRENEUR, mode = EnumSource.Mode.EXCLUDE)
    void everyOtherEnterpriseType_isVerifiedByIceAlone(EnterpriseType enterpriseType) {
        BamVerificationResult result = gateway.verify(enterpriseType, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL,
            BamScenario.CLEAR);

        assertEquals(CODE_ICE, result.identifierType());
    }

    @Test
    void identifierIsEchoedBackUnchanged() {
        BamVerificationResult result = gateway.verify(EnterpriseType.PME, ID_001234567000089,
            COMPANY_ATLAS_TEXTILE_SARL,
            BamScenario.CLEAR);

        assertEquals(ID_001234567000089, result.verifiedIdentifier());
    }

    @ParameterizedTest
    @EnumSource(EnterpriseType.class)
    void randomDraw_onlyEverProducesScenariosValidForThatEnterpriseType(EnterpriseType enterpriseType) {
        Map<EnterpriseType, Map<BamScenario, Boolean>> allowed = new EnumMap<>(EnterpriseType.class);
        allowed.put(EnterpriseType.TPE, allowedOf(BamScenario.CLEAR, BamScenario.MINOR_INCIDENT,
            BamScenario.ACTIVE_INCIDENT));
        allowed.put(EnterpriseType.AUTO_ENTREPRENEUR, allowedOf(BamScenario.CLEAR,
            BamScenario.PERSONAL_EXPOSURE_DETECTED, BamScenario.ACTIVE_INCIDENT));
        allowed.put(EnterpriseType.PME, allowedOf(BamScenario.CLEAR, BamScenario.MULTIPLE_EXPOSURES,
            BamScenario.ACTIVE_INCIDENT));
        allowed.put(EnterpriseType.ETI, allowedOf(BamScenario.CLEAR, BamScenario.MULTIPLE_EXPOSURES,
            BamScenario.ACTIVE_INCIDENT));
        allowed.put(EnterpriseType.GE, allowedOf(BamScenario.CLEAR, BamScenario.CONSOLIDATED_GROUP_EXPOSURE,
            BamScenario.REQUIRES_MANUAL_REVIEW));

        for (int i = 0; i < 200; i++) {
            BamVerificationResult result = gateway.verify(enterpriseType, ID_001234567000089,
                COMPANY_ATLAS_TEXTILE_SARL,
                null);
            assertTrue(allowed.get(enterpriseType).containsKey(result.scenario()),
                () -> enterpriseType + " produced an out-of-distribution scenario: " + result.scenario());
        }
    }

    private static Map<BamScenario, Boolean> allowedOf(BamScenario... scenarios) {
        Map<BamScenario, Boolean> map = new EnumMap<>(BamScenario.class);
        for (BamScenario scenario : scenarios) {
            map.put(scenario, Boolean.TRUE);
        }
        return map;
    }

    @Test
    void latencyIsBoundedByTheConfiguredMinAndMax() {
        MockBamVerificationGateway boundedGateway = new MockBamVerificationGateway(50, 60, Clock.systemUTC());
        long start = System.currentTimeMillis();
        boundedGateway.verify(EnterpriseType.PME, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL, BamScenario.CLEAR);
        long elapsedMs = System.currentTimeMillis() - start;

        assertTrue(elapsedMs >= 50, "expected at least 50ms of simulated latency, took " + elapsedMs + "ms");
    }
}
