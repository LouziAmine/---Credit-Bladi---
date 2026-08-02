package com.credit.bladi.unittest.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.creditrate.request.PatchCreditRateDTO;
import com.credit.bladi.controller.dto.creditrate.response.CreditRateResponseDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CreditRateDtoTest {

    @Test
    void shouldSetAndGetPatchRequestFields() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();
        dto.setRateMin(new BigDecimal("3.50"));
        dto.setRateMax(new BigDecimal("5.50"));
        dto.setMinDurationMonths(12);
        dto.setMaxDurationMonths(300);
        dto.setActive(true);

        assertEquals(new BigDecimal("3.50"), dto.getRateMin());
        assertEquals(new BigDecimal("5.50"), dto.getRateMax());
        assertEquals(12, dto.getMinDurationMonths());
        assertEquals(300, dto.getMaxDurationMonths());
        assertTrue(dto.getActive());
    }

    @Test
    void shouldPatchRequestEqualsAndHashCode() {
        PatchCreditRateDTO d1 = new PatchCreditRateDTO();
        d1.setRateMin(new BigDecimal("3.50"));

        PatchCreditRateDTO d2 = new PatchCreditRateDTO();
        d2.setRateMin(new BigDecimal("3.50"));

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldPatchRequestNotEqualToDifferentType() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldPatchRequestNotEqualWhenDifferentFields() {
        PatchCreditRateDTO d1 = new PatchCreditRateDTO();
        d1.setRateMin(new BigDecimal("3.50"));

        PatchCreditRateDTO d2 = new PatchCreditRateDTO();
        d2.setRateMin(new BigDecimal("4.50"));

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldPatchRequestToString() {
        PatchCreditRateDTO dto = new PatchCreditRateDTO();
        dto.setRateMin(new BigDecimal("3.50"));
        dto.setActive(true);
        String str = dto.toString();
        assertTrue(str.contains("3.50"));
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldSetAndGetResponseFields() {
        CreditRateResponseDTO dto = new CreditRateResponseDTO();
        dto.setId(1L);
        dto.setCspCode("SALA");
        dto.setRateMin(new BigDecimal("3.50"));
        dto.setRateMax(new BigDecimal("5.50"));
        dto.setMinDurationMonths(12);
        dto.setMaxDurationMonths(300);
        dto.setActive(true);

        assertEquals(1L, dto.getId());
        assertEquals("SALA", dto.getCspCode());
        assertEquals(new BigDecimal("3.50"), dto.getRateMin());
        assertEquals(new BigDecimal("5.50"), dto.getRateMax());
        assertEquals(12, dto.getMinDurationMonths());
        assertEquals(300, dto.getMaxDurationMonths());
        assertTrue(dto.getActive());
    }

    @Test
    void shouldResponseEqualsAndHashCode() {
        CreditRateResponseDTO d1 = new CreditRateResponseDTO();
        d1.setId(1L);

        CreditRateResponseDTO d2 = new CreditRateResponseDTO();
        d2.setId(1L);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldResponseNotEqualToDifferentType() {
        CreditRateResponseDTO dto = new CreditRateResponseDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldResponseNotEqualWhenDifferentFields() {
        CreditRateResponseDTO d1 = new CreditRateResponseDTO();
        d1.setId(1L);

        CreditRateResponseDTO d2 = new CreditRateResponseDTO();
        d2.setId(2L);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldResponseToString() {
        CreditRateResponseDTO dto = new CreditRateResponseDTO();
        dto.setId(1L);
        dto.setCspCode("SALA");
        String str = dto.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("SALA"));
    }
}
