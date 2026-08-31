package com.credit.bladi.unittest.controller.verification;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_4967525;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_EXPOSURE_DETECTED;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ICE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_IN_LITIGATION;
import static com.credit.bladi.unittest.mock.data.MockCodes.ID_004573986899689;
import static com.credit.bladi.unittest.mock.data.MockContacts.COMPANY_ATLAS_BANK;
import static com.credit.bladi.unittest.mock.data.MockDates.DATE_2026_10_11T22_00_00Z;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.credit.bladi.controller.dto.verification.response.BamVerificationResponseDTO;
import com.credit.bladi.controller.verification.mapper.BamVerificationMapper;
import com.credit.bladi.model.enums.BamScenario;
import com.credit.bladi.model.enums.CentraleRisquesStatus;
import com.credit.bladi.model.enums.RepaymentStatus;
import com.credit.bladi.model.enums.ScipStatus;
import com.credit.bladi.model.enums.SimulatedRecommendation;
import com.credit.bladi.model.verification.BamVerificationResult;
import com.credit.bladi.model.verification.OutstandingLoan;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

class BamVerificationMapperTest {
    @Test
    void shouldExposeRepaymentStatusLikeEveryOtherEnumOfTheResponse() {
        StaticMessageSource messages = new StaticMessageSource();
        messages.addMessage("bamVerification.disclaimer", Locale.getDefault(), "simulated");
        OutstandingLoan loan = new OutstandingLoan(COMPANY_ATLAS_BANK, "Investment credit", AMOUNT_4967525,
            RepaymentStatus.IN_LITIGATION);
        BamVerificationResult result = new BamVerificationResult(ID_004573986899689, CODE_ICE, ScipStatus.CLEAR,
            List.of(), CentraleRisquesStatus.EXPOSURE_DETECTED, List.of(loan), SimulatedRecommendation.TO_REVIEW,
            BamScenario.PERSONAL_EXPOSURE_DETECTED, Instant.parse(DATE_2026_10_11T22_00_00Z));

        BamVerificationResponseDTO dto = new BamVerificationMapper(messages).toResponseDTO(result);

        assertEquals(CODE_IN_LITIGATION, dto.centraleRisquesResult().outstandingLoans().get(0).repaymentStatus());
        assertEquals(CODE_EXPOSURE_DETECTED, dto.centraleRisquesResult().status());
    }
}
