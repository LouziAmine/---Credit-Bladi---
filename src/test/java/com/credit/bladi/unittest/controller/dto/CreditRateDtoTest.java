package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_4_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_50;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_3_50;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_AVERAGE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_SALA;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreditRateDtoTest {
    private static final UUID ID_1 = UUID.randomUUID();

    private static final UUID ID_2 = UUID.randomUUID();

    @Test
    void shouldSetAndGetPatchRequestFields() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(
            DECIMAL_3_50, DECIMAL_5_50, 12, 300, CODE_AVERAGE, true);

        assertEquals(DECIMAL_3_50, dto.rateMin());
        assertEquals(DECIMAL_5_50, dto.rateMax());
        assertEquals(12, dto.minDurationMonths());
        assertEquals(300, dto.maxDurationMonths());
        assertEquals(CODE_AVERAGE, dto.rateFormula());
        assertTrue(dto.active());
    }

    @Test
    void shouldPatchRequestEqualsAndHashCode() {
        PatchCreditRateDTO d1 = new PatchCreditRateDTO(DECIMAL_3_50, null, null, null, null, null);
        PatchCreditRateDTO d2 = new PatchCreditRateDTO(DECIMAL_3_50, null, null, null, null, null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldPatchRequestNotEqualToDifferentType() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldPatchRequestNotEqualWhenDifferentFields() {
        PatchCreditRateDTO d1 = new PatchCreditRateDTO(DECIMAL_3_50, null, null, null, null, null);
        PatchCreditRateDTO d2 = new PatchCreditRateDTO(DECIMAL_4_50, null, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldPatchRequestToString() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO(DECIMAL_3_50, null, null, null, null, true);
        String str = dto.toString();
        assertTrue(str.contains(TEXT_3_50));
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldSetAndGetResponseFields() {
        CreditRateResponseDTO dto = new CreditRateResponseDTO(
            ID_1, CODE_SALA, DECIMAL_3_50, DECIMAL_5_50, CODE_AVERAGE, 12, 300, true);

        assertEquals(ID_1, dto.id());
        assertEquals(CODE_SALA, dto.cspCode());
        assertEquals(DECIMAL_3_50, dto.rateMin());
        assertEquals(DECIMAL_5_50, dto.rateMax());
        assertEquals(CODE_AVERAGE, dto.rateFormula());
        assertEquals(12, dto.minDurationMonths());
        assertEquals(300, dto.maxDurationMonths());
        assertTrue(dto.active());
    }

    @Test
    void shouldResponseEqualsAndHashCode() {
        CreditRateResponseDTO d1 = new CreditRateResponseDTO(ID_1, null, null, null, null, null, null, null);
        CreditRateResponseDTO d2 = new CreditRateResponseDTO(ID_1, null, null, null, null, null, null, null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldResponseNotEqualToDifferentType() {
        CreditRateResponseDTO dto = new CreditRateResponseDTO(null, null, null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldResponseNotEqualWhenDifferentFields() {
        CreditRateResponseDTO d1 = new CreditRateResponseDTO(ID_1, null, null, null, null, null, null, null);
        CreditRateResponseDTO d2 = new CreditRateResponseDTO(ID_2, null, null, null, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldResponseToString() {
        CreditRateResponseDTO dto = new CreditRateResponseDTO(ID_1, CODE_SALA, null, null, null, null, null, null);
        String str = dto.toString();
        assertTrue(str.contains(ID_1.toString()));
        assertTrue(str.contains(CODE_SALA));
    }
}
