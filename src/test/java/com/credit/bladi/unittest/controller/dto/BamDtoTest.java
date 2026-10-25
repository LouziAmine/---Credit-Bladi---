package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_20000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_01;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_015;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_40;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_80;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.bam.request.PatchBamParametersDTO;
import com.credit.bladi.controller.dto.bam.response.BamParametersResponseDTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BamDtoTest {
    private static final UUID ID_1 = UUID.randomUUID();

    private static final UUID ID_2 = UUID.randomUUID();

    @Test
    void shouldSetAndGetPatchBamParametersFields() {
        PatchBamParametersDTO dto = new PatchBamParametersDTO(
            DECIMAL_0_01,
            AMOUNT_5000,
            AMOUNT_20000,
            DECIMAL_0_015,
            DECIMAL_0_015,
            DECIMAL_0_01,
            DECIMAL_0_80,
            DECIMAL_0_40,
            70,
            DECIMAL_0_0004,
            true);

        assertEquals(DECIMAL_0_01, dto.processingFeesRatio());
        assertEquals(AMOUNT_5000, dto.minProcessingFees());
        assertEquals(AMOUNT_20000, dto.maxProcessingFees());
        assertEquals(DECIMAL_0_015, dto.registrationFeeRatio());
        assertEquals(DECIMAL_0_015, dto.landConservationRatio());
        assertEquals(DECIMAL_0_01, dto.notaryFeeRatio());
        assertEquals(DECIMAL_0_80, dto.ltvRatio());
        assertEquals(DECIMAL_0_40, dto.maxDebtRatio());
        assertEquals(70, dto.maxAgeAtMaturity());
        assertEquals(DECIMAL_0_0004, dto.adiMonthlyRate());
        assertTrue(dto.active());
    }

    @Test
    void shouldPatchBamParametersEqualsAndHashCode() {
        PatchBamParametersDTO d1 = new PatchBamParametersDTO(
            null, null, null, null, null, null, null, null, null, null, true);
        PatchBamParametersDTO d2 = new PatchBamParametersDTO(
            null, null, null, null, null, null, null, null, null, null, true);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldPatchBamParametersNotBeEqualWhenDifferentFields() {
        PatchBamParametersDTO d1 = new PatchBamParametersDTO(
            null, null, null, null, null, null, null, null, null, null, true);
        PatchBamParametersDTO d2 = new PatchBamParametersDTO(
            null, null, null, null, null, null, null, null, null, null, false);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldPatchBamParametersNotBeEqualToDifferentType() {
        PatchBamParametersDTO dto = new PatchBamParametersDTO(
            null, null, null, null, null, null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldPatchBamParametersToString() {
        PatchBamParametersDTO dto = new PatchBamParametersDTO(
            null, null, null, null, null, null, null, null, null, null, true);
        String str = dto.toString();
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldSetAndGetBamParametersResponseFields() {
        BamParametersResponseDTO dto = new BamParametersResponseDTO(
            ID_1,
            DECIMAL_0_01,
            AMOUNT_5000,
            AMOUNT_20000,
            DECIMAL_0_015,
            DECIMAL_0_015,
            DECIMAL_0_01,
            DECIMAL_0_80,
            DECIMAL_0_40,
            70,
            DECIMAL_0_0004,
            true);

        assertEquals(ID_1, dto.id());
        assertEquals(DECIMAL_0_01, dto.processingFeesRatio());
        assertNotNull(dto.minProcessingFees());
        assertNotNull(dto.maxProcessingFees());
        assertEquals(DECIMAL_0_015, dto.registrationFeeRatio());
        assertEquals(DECIMAL_0_015, dto.landConservationRatio());
        assertEquals(DECIMAL_0_01, dto.notaryFeeRatio());
        assertEquals(DECIMAL_0_80, dto.ltvRatio());
        assertEquals(DECIMAL_0_40, dto.maxDebtRatio());
        assertEquals(70, dto.maxAgeAtMaturity());
        assertEquals(DECIMAL_0_0004, dto.adiMonthlyRate());
        assertTrue(dto.active());
    }

    @Test
    void shouldBamParametersResponseEqualsAndHashCode() {
        BamParametersResponseDTO d1 = new BamParametersResponseDTO(
            ID_1, null, null, null, null, null, null, null, null, null, null, true);
        BamParametersResponseDTO d2 = new BamParametersResponseDTO(
            ID_1, null, null, null, null, null, null, null, null, null, null, true);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldBamParametersResponseNotBeEqualWhenDifferentFields() {
        BamParametersResponseDTO d1 = new BamParametersResponseDTO(
            ID_1, null, null, null, null, null, null, null, null, null, null, null);
        BamParametersResponseDTO d2 = new BamParametersResponseDTO(
            ID_2, null, null, null, null, null, null, null, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldBamParametersResponseNotBeEqualToDifferentType() {
        BamParametersResponseDTO dto = new BamParametersResponseDTO(
            null, null, null, null, null, null, null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldBamParametersResponseToString() {
        BamParametersResponseDTO dto = new BamParametersResponseDTO(
            ID_1, null, null, null, null, null, null, null, null, null, null, null);
        String str = dto.toString();
        assertTrue(str.contains(ID_1.toString()));
    }
}
