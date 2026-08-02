package com.credit.bladi.unittest.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO;
import com.credit.bladi.controller.dto.mourabaha.response.MourabahaParametersResponseDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MourabahaDtoTest {

    @Test
    void shouldSetAndGetPatchRequestFields() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO();
        dto.setProfitRate(new BigDecimal("5.25"));
        dto.setVatRate(new BigDecimal("0.10"));
        dto.setActive(true);

        assertEquals(new BigDecimal("5.25"), dto.getProfitRate());
        assertEquals(new BigDecimal("0.10"), dto.getVatRate());
        assertTrue(dto.getActive());
    }

    @Test
    void shouldPatchRequestEqualsAndHashCode() {
        PatchMourabahaParametersDTO d1 = new PatchMourabahaParametersDTO();
        d1.setProfitRate(new BigDecimal("5.25"));

        PatchMourabahaParametersDTO d2 = new PatchMourabahaParametersDTO();
        d2.setProfitRate(new BigDecimal("5.25"));

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldPatchRequestNotEqualToDifferentType() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldPatchRequestNotEqualWhenDifferentFields() {
        PatchMourabahaParametersDTO d1 = new PatchMourabahaParametersDTO();
        d1.setProfitRate(new BigDecimal("5.25"));

        PatchMourabahaParametersDTO d2 = new PatchMourabahaParametersDTO();
        d2.setProfitRate(new BigDecimal("6.25"));

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldPatchRequestToString() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO();
        dto.setProfitRate(new BigDecimal("5.25"));
        dto.setActive(true);
        String str = dto.toString();
        assertTrue(str.contains("5.25"));
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldSetAndGetResponseFields() {
        MourabahaParametersResponseDTO dto = new MourabahaParametersResponseDTO();
        dto.setId(1L);
        dto.setProfitRate(new BigDecimal("5.25"));
        dto.setVatRate(new BigDecimal("0.10"));
        dto.setActive(true);

        assertEquals(1L, dto.getId());
        assertEquals(new BigDecimal("5.25"), dto.getProfitRate());
        assertEquals(new BigDecimal("0.10"), dto.getVatRate());
        assertTrue(dto.getActive());
    }

    @Test
    void shouldResponseEqualsAndHashCode() {
        MourabahaParametersResponseDTO d1 = new MourabahaParametersResponseDTO();
        d1.setId(1L);

        MourabahaParametersResponseDTO d2 = new MourabahaParametersResponseDTO();
        d2.setId(1L);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldResponseNotEqualToDifferentType() {
        MourabahaParametersResponseDTO dto = new MourabahaParametersResponseDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldResponseNotEqualWhenDifferentFields() {
        MourabahaParametersResponseDTO d1 = new MourabahaParametersResponseDTO();
        d1.setId(1L);

        MourabahaParametersResponseDTO d2 = new MourabahaParametersResponseDTO();
        d2.setId(2L);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldResponseToString() {
        MourabahaParametersResponseDTO dto = new MourabahaParametersResponseDTO();
        dto.setId(1L);
        dto.setActive(true);
        String str = dto.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("true"));
    }
}