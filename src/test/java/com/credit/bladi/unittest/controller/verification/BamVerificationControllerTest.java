package com.credit.bladi.unittest.controller.verification;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ACTIVE_INCIDENT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLEAR;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_FAVORABLE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ICE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MOCK_SIMULATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_NOT_A_REAL_SCENARIO;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_NOT_A_REAL_TYPE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PME;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_TPE;
import static com.credit.bladi.unittest.mock.data.MockCodes.ID_001234567000089;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_TEXTILE_SARL;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_09_06T10_00_00Z;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.dto.verification.request.BamVerificationRequestDTO;
import com.credit.bladi.controller.dto.verification.response.BamVerificationResponseDTO;
import com.credit.bladi.controller.dto.verification.response.CentraleRisquesResultResponseDTO;
import com.credit.bladi.controller.dto.verification.response.ScipResultResponseDTO;
import com.credit.bladi.controller.verification.BamVerificationController;
import com.credit.bladi.controller.verification.mapper.BamVerificationMapper;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.CentraleRisquesStatus;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.ScipStatus;
import com.credit.bladi.model.enums.SimulatedRecommendation;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.service.verification.IBamVerificationService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BamVerificationControllerTest {
    @Mock
    private IBamVerificationService bamVerificationService;

    @Mock
    private BamVerificationMapper bamVerificationMapper;

    private BamVerificationController controller;

    private static final BamVerificationResult CLEAR_RESULT = new BamVerificationResult(
        ID_001234567000089, CODE_ICE, ScipStatus.CLEAR, List.of(), CentraleRisquesStatus.CLEAR, List.of(),
        SimulatedRecommendation.FAVORABLE, BamScenario.CLEAR, Instant.parse(DATE_2026_09_06T10_00_00Z));

    private static final BamVerificationResponseDTO CLEAR_RESPONSE_DTO = new BamVerificationResponseDTO(
        CODE_MOCK_SIMULATION, "disclaimer", ID_001234567000089, CODE_ICE,
        new ScipResultResponseDTO(CODE_CLEAR, List.of()), new CentraleRisquesResultResponseDTO(CODE_CLEAR, List.of()),
        CODE_FAVORABLE, Instant.parse(DATE_2026_09_06T10_00_00Z));

    @BeforeEach
    void setUp() {
        controller = new BamVerificationController(bamVerificationService, bamVerificationMapper);
    }

    private static BamVerificationRequestDTO request(String enterpriseType) {
        return new BamVerificationRequestDTO(enterpriseType, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL);
    }

    @Test
    void verify_parsesEnterpriseTypeAndDelegatesToTheServiceWithNoForcedScenario() {
        when(bamVerificationService.verify(EnterpriseType.PME, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL, null))
            .thenReturn(CLEAR_RESULT);
        when(bamVerificationMapper.toResponseDTO(CLEAR_RESULT)).thenReturn(CLEAR_RESPONSE_DTO);

        ResponseEntity<BamVerificationResponseDTO> response = controller.verify(request(CODE_PME), null);

        assertEquals(CODE_MOCK_SIMULATION, response.getBody().source());
        assertEquals(CODE_CLEAR, response.getBody().scipResult().status());
    }

    @Test
    void verify_parsesTheForcedScenarioQueryParamAndPassesItToTheService() {
        when(bamVerificationService.verify(EnterpriseType.TPE, ID_001234567000089, COMPANY_ATLAS_TEXTILE_SARL,
            BamScenario.ACTIVE_INCIDENT)).thenReturn(CLEAR_RESULT);
        when(bamVerificationMapper.toResponseDTO(CLEAR_RESULT)).thenReturn(CLEAR_RESPONSE_DTO);

        ResponseEntity<BamVerificationResponseDTO> response = controller.verify(request(CODE_TPE),
            CODE_ACTIVE_INCIDENT);

        assertEquals(CODE_MOCK_SIMULATION, response.getBody().source());
    }

    @Test
    void verify_rejectsAnUnknownScenarioValue() {
        assertThrows(ValidationException.class, () -> controller.verify(request(CODE_TPE), CODE_NOT_A_REAL_SCENARIO));
    }

    @Test
    void verify_rejectsAnUnknownEnterpriseTypeValue() {
        assertThrows(ValidationException.class, () -> controller.verify(request(CODE_NOT_A_REAL_TYPE), null));
    }
}
