package com.credit.bladi.unittest.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class BamDtoTest {

    @Test
    void shouldSetAndGetPatchBamParametersFields() {
        PatchBamParametersDTO dto = new PatchBamParametersDTO();
        dto.setProcessingFeesRatio(new BigDecimal("0.01"));
        dto.setMinProcessingFees(new BigDecimal("5000"));
        dto.setMaxProcessingFees(new BigDecimal("20000"));
        dto.setRegistrationFeeRatio(new BigDecimal("0.015"));
        dto.setLandConservationRatio(new BigDecimal("0.015"));
        dto.setNotaryFeeRatio(new BigDecimal("0.01"));
        dto.setLtvRatio(new BigDecimal("0.80"));
        dto.setMaxDebtRatio(new BigDecimal("0.40"));
        dto.setMaxAgeAtMaturity(70);
        dto.setAdiMonthlyRate(new BigDecimal("0.0004"));
        dto.setActive(true);

        assertEquals(new BigDecimal("0.01"), dto.getProcessingFeesRatio());
        assertEquals(new BigDecimal("5000"), dto.getMinProcessingFees());
        assertEquals(new BigDecimal("20000"), dto.getMaxProcessingFees());
        assertEquals(new BigDecimal("0.015"), dto.getRegistrationFeeRatio());
        assertEquals(new BigDecimal("0.015"), dto.getLandConservationRatio());
        assertEquals(new BigDecimal("0.01"), dto.getNotaryFeeRatio());
        assertEquals(new BigDecimal("0.80"), dto.getLtvRatio());
        assertEquals(new BigDecimal("0.40"), dto.getMaxDebtRatio());
        assertEquals(70, dto.getMaxAgeAtMaturity());
        assertEquals(new BigDecimal("0.0004"), dto.getAdiMonthlyRate());
        assertTrue(dto.getActive());
    }

    @Test
    void shouldPatchBamParametersEqualsAndHashCode() {
        PatchBamParametersDTO d1 = new PatchBamParametersDTO();
        d1.setActive(true);

        PatchBamParametersDTO d2 = new PatchBamParametersDTO();
        d2.setActive(true);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldPatchBamParametersNotBeEqualWhenDifferentFields() {
        PatchBamParametersDTO d1 = new PatchBamParametersDTO();
        d1.setActive(true);

        PatchBamParametersDTO d2 = new PatchBamParametersDTO();
        d2.setActive(false);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldPatchBamParametersNotBeEqualToDifferentType() {
        PatchBamParametersDTO dto = new PatchBamParametersDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldPatchBamParametersToString() {
        PatchBamParametersDTO dto = new PatchBamParametersDTO();
        dto.setActive(true);
        String str = dto.toString();
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldSetAndGetBamParametersResponseFields() {
        BamParametersResponseDTO dto = new BamParametersResponseDTO();
        dto.setId(1L);
        dto.setProcessingFeesRatio(new BigDecimal("0.01"));
        dto.setMinProcessingFees(new BigDecimal("5000"));
        dto.setMaxProcessingFees(new BigDecimal("20000"));
        dto.setRegistrationFeeRatio(new BigDecimal("0.015"));
        dto.setLandConservationRatio(new BigDecimal("0.015"));
        dto.setNotaryFeeRatio(new BigDecimal("0.01"));
        dto.setLtvRatio(new BigDecimal("0.80"));
        dto.setMaxDebtRatio(new BigDecimal("0.40"));
        dto.setMaxAgeAtMaturity(70);
        dto.setAdiMonthlyRate(new BigDecimal("0.0004"));
        dto.setActive(true);

        assertEquals(1L, dto.getId());
        assertEquals(new BigDecimal("0.01"), dto.getProcessingFeesRatio());
        assertNotNull(dto.getMinProcessingFees());
        assertNotNull(dto.getMaxProcessingFees());
        assertEquals(new BigDecimal("0.015"), dto.getRegistrationFeeRatio());
        assertEquals(new BigDecimal("0.015"), dto.getLandConservationRatio());
        assertEquals(new BigDecimal("0.01"), dto.getNotaryFeeRatio());
        assertEquals(new BigDecimal("0.80"), dto.getLtvRatio());
        assertEquals(new BigDecimal("0.40"), dto.getMaxDebtRatio());
        assertEquals(70, dto.getMaxAgeAtMaturity());
        assertEquals(new BigDecimal("0.0004"), dto.getAdiMonthlyRate());
        assertTrue(dto.getActive());
    }

    @Test
    void shouldBamParametersResponseEqualsAndHashCode() {
        BamParametersResponseDTO d1 = new BamParametersResponseDTO();
        d1.setId(1L);
        d1.setActive(true);

        BamParametersResponseDTO d2 = new BamParametersResponseDTO();
        d2.setId(1L);
        d2.setActive(true);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldBamParametersResponseNotBeEqualWhenDifferentFields() {
        BamParametersResponseDTO d1 = new BamParametersResponseDTO();
        d1.setId(1L);

        BamParametersResponseDTO d2 = new BamParametersResponseDTO();
        d2.setId(2L);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldBamParametersResponseNotBeEqualToDifferentType() {
        BamParametersResponseDTO dto = new BamParametersResponseDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldBamParametersResponseToString() {
        BamParametersResponseDTO dto = new BamParametersResponseDTO();
        dto.setId(5L);
        String str = dto.toString();
        assertTrue(str.contains("5"));
    }
}