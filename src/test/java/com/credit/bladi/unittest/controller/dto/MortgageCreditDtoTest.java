package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_105;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_110;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1155;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_12000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_168000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_220000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_24000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_25000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2800;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_28000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2890;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3100;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3200;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3310;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3439;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_3489;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_572000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_600000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_668000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_72000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_744000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_7500;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_90;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_80;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_500000;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CONVENTIONAL;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MA;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SALA;
import static com.credit.bladi.unittest.mock.data.MockCodes.REFERENCE_DEM_3F2A9C1B;
import static com.credit.bladi.unittest.mock.data.MockContacts.EMAIL_KARIM_BENALI_AT_EXAMPLE_MA;
import static com.credit.bladi.unittest.mock.data.MockContacts.PERSON_KARIM_BENALI;
import static com.credit.bladi.unittest.mock.data.MockContacts.PHONE_0612345678;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.AmortizationRowDTO;
import com.credit.bladi.controller.dto.mortgage.request.MortgageCreditRequestDTO;
import com.credit.bladi.controller.dto.mortgage.response.ConventionalFinancingResponseDTO;
import com.credit.bladi.controller.dto.mortgage.response.MortgageCreditResponseDTO;
import com.credit.bladi.controller.dto.mortgage.response.MourabahaFinancingResponseDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MortgageCreditDtoTest {
    @Test
    void shouldSetAndGetSimulationRequestFields() {
        MortgageCreditRequestDTO dto = new MortgageCreditRequestDTO(
            PERSON_KARIM_BENALI, EMAIL_KARIM_BENALI_AT_EXAMPLE_MA, PHONE_0612345678,
            AMOUNT_500000, 240, CODE_SALA, null, CODE_MA, true, null, null, 40, true, null);

        assertEquals(AMOUNT_500000, dto.amount());
        assertEquals(240, dto.duration());
        assertEquals(CODE_SALA, dto.cspCode());
        assertEquals(CODE_MA, dto.nationalityCode());
        assertTrue(dto.moroccanResident());
        assertEquals(40, dto.age());
        assertTrue(dto.coBorrower());
    }

    @Test
    void shouldSimulationRequestEqualsAndHashCode() {
        MortgageCreditRequestDTO d1 = new MortgageCreditRequestDTO(
            null, null, null, AMOUNT_500000, 240, null, null, null, null, null, null, null, null, null);
        MortgageCreditRequestDTO d2 = new MortgageCreditRequestDTO(
            null, null, null, AMOUNT_500000, 240, null, null, null, null, null, null, null, null, null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldSimulationRequestNotEqualWhenDifferentFields() {
        MortgageCreditRequestDTO d1 = new MortgageCreditRequestDTO(
            null, null, null, AMOUNT_500000, null, null, null, null, null, null, null, null, null, null);
        MortgageCreditRequestDTO d2 = new MortgageCreditRequestDTO(
            null, null, null, AMOUNT_600000, null, null, null, null, null, null, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldSimulationRequestNotEqualToDifferentType() {
        MortgageCreditRequestDTO dto = new MortgageCreditRequestDTO(
            null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldSimulationRequestToString() {
        MortgageCreditRequestDTO dto = new MortgageCreditRequestDTO(
            null, null, null, AMOUNT_500000, null, CODE_SALA, null, null, null, null, null, null, null,
            null);
        String str = dto.toString();
        assertTrue(str.contains(CODE_SALA));
    }

    @Test
    void shouldSetAndGetSimulationResponseFields() {
        ConventionalFinancingResponseDTO conventional = new ConventionalFinancingResponseDTO(
            DECIMAL_3_50,
            DECIMAL_5_50,
            AMOUNT_3000,
            AMOUNT_100,
            AMOUNT_3100,
            AMOUNT_220000,
            AMOUNT_24000,
            AMOUNT_744000,
            AMOUNT_2800,
            AMOUNT_3200,
            AMOUNT_90,
            AMOUNT_110,
            AMOUNT_2890,
            AMOUNT_3310,
            AMOUNT_72000,
            AMOUNT_168000,
            AMOUNT_20000,
            AMOUNT_28000,
            AMOUNT_572000,
            AMOUNT_668000);
        MourabahaFinancingResponseDTO mourabaha = new MourabahaFinancingResponseDTO(
            AMOUNT_1000,
            AMOUNT_105,
            AMOUNT_1155,
            AMOUNT_3439,
            AMOUNT_50,
            AMOUNT_3489,
            AMOUNT_12000,
            AMOUNT_1155);
        MortgageCreditResponseDTO dto = new MortgageCreditResponseDTO(
            AMOUNT_500000,
            240,
            CODE_SALA,
            CODE_CONVENTIONAL,
            CODE_MA,
            true,
            40,
            AMOUNT_20000,
            DECIMAL_4_80,
            DECIMAL_5_10,
            AMOUNT_7500,
            AMOUNT_7500,
            AMOUNT_5000,
            AMOUNT_5000,
            AMOUNT_25000,
            conventional,
            mourabaha,
            null,
            REFERENCE_DEM_3F2A9C1B);

        assertEquals(AMOUNT_500000, dto.amount());
        assertEquals(240, dto.duration());
        assertEquals(CODE_SALA, dto.cspCode());
        assertEquals(CODE_CONVENTIONAL, dto.productType());
        assertEquals(CODE_MA, dto.nationalityCode());
        assertTrue(dto.moroccanResident());
        assertEquals(40, dto.age());
        assertEquals(AMOUNT_20000, dto.totalMonthlyIncome());
        assertEquals(DECIMAL_4_80, dto.applicableRate());
        assertEquals(DECIMAL_5_10, dto.taeg());
        assertEquals(AMOUNT_7500, dto.registrationFees());
        assertEquals(AMOUNT_7500, dto.landConservationFees());
        assertEquals(AMOUNT_5000, dto.notaryFees());
        assertEquals(AMOUNT_5000, dto.processingFees());
        assertEquals(AMOUNT_25000, dto.totalFees());
        assertEquals(DECIMAL_3_50, dto.conventional().rateMin());
        assertEquals(DECIMAL_5_50, dto.conventional().rateMax());
        assertEquals(AMOUNT_3000, dto.conventional().monthlyPayment());
        assertEquals(AMOUNT_100, dto.conventional().adiMonthlyPremium());
        assertEquals(AMOUNT_3100, dto.conventional().allInMonthlyPayment());
        assertEquals(AMOUNT_220000, dto.conventional().totalInterest());
        assertEquals(AMOUNT_24000, dto.conventional().totalAdiCost());
        assertEquals(AMOUNT_744000, dto.conventional().totalCost());
        assertEquals(AMOUNT_2800, dto.conventional().monthlyPaymentMin());
        assertEquals(AMOUNT_3200, dto.conventional().monthlyPaymentMax());
        assertEquals(AMOUNT_90, dto.conventional().adiMonthlyPremiumMin());
        assertEquals(AMOUNT_110, dto.conventional().adiMonthlyPremiumMax());
        assertEquals(AMOUNT_2890, dto.conventional().allInMonthlyPaymentMin());
        assertEquals(AMOUNT_3310, dto.conventional().allInMonthlyPaymentMax());
        assertEquals(AMOUNT_72000, dto.conventional().totalInterestMin());
        assertEquals(AMOUNT_168000, dto.conventional().totalInterestMax());
        assertEquals(AMOUNT_20000, dto.conventional().totalAdiCostMin());
        assertEquals(AMOUNT_28000, dto.conventional().totalAdiCostMax());
        assertEquals(AMOUNT_572000, dto.conventional().totalCostMin());
        assertEquals(AMOUNT_668000, dto.conventional().totalCostMax());
        assertEquals(AMOUNT_105, dto.mourabaha().vatOnMargin());
        assertEquals(AMOUNT_1155, dto.mourabaha().marginTtc());
        assertEquals(AMOUNT_3439, dto.mourabaha().monthlyPaymentTtc());
        assertEquals(AMOUNT_50, dto.mourabaha().takafulMonthlyContribution());
        assertEquals(AMOUNT_3489, dto.mourabaha().allInMonthlyPaymentTtc());
        assertEquals(AMOUNT_12000, dto.mourabaha().totalTakafulCost());
        assertEquals(REFERENCE_DEM_3F2A9C1B, dto.demandReference());
    }

    @Test
    void shouldSimulationResponseEqualsAndHashCode() {
        MortgageCreditResponseDTO d1 = emptyResponseWith(AMOUNT_500000, 240);
        MortgageCreditResponseDTO d2 = emptyResponseWith(AMOUNT_500000, 240);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldSimulationResponseNotEqualWhenDifferentFields() {
        MortgageCreditResponseDTO d1 = emptyResponseWith(AMOUNT_500000, null);
        MortgageCreditResponseDTO d2 = emptyResponseWith(AMOUNT_600000, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldSimulationResponseNotEqualToDifferentType() {
        MortgageCreditResponseDTO dto = emptyResponseWith(null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldSimulationResponseToStringWithAmortizationSchedule() {
        MortgageCreditResponseDTO dto = new MortgageCreditResponseDTO(
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            java.util.List.of(new AmortizationRowDTO(null, null, null, null, null, null, null, null, null)), null);
        String str = dto.toString();
        assertTrue(str.contains("AmortizationRowDTO"));
    }

    @Test
    void shouldSimulationResponseToString() {
        MortgageCreditResponseDTO dto = emptyResponseWith(AMOUNT_500000, null, CODE_SALA);
        String str = dto.toString();
        assertTrue(str.contains(TEXT_500000));
        assertTrue(str.contains(CODE_SALA));
    }

    private static MortgageCreditResponseDTO emptyResponseWith(BigDecimal amount, Integer duration) {
        return emptyResponseWith(amount, duration, null);
    }

    private static MortgageCreditResponseDTO emptyResponseWith(BigDecimal amount, Integer duration, String cspCode) {
        return new MortgageCreditResponseDTO(
            amount, duration, cspCode,
            null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null);
    }
}
