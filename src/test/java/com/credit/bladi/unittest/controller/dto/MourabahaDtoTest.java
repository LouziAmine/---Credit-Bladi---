package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_0004;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_0_10;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_6_25;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_5_25;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.mourabaha.request.PatchMourabahaParametersDTO;
import com.credit.bladi.controller.dto.mourabaha.response.MourabahaParametersResponseDTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MourabahaDtoTest {
    private static final UUID ID_1 = UUID.randomUUID();

    private static final UUID ID_2 = UUID.randomUUID();

    @Test
    void shouldSetAndGetPatchRequestFields() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO(
            DECIMAL_5_25, DECIMAL_0_10, DECIMAL_0_0004, true);

        assertEquals(DECIMAL_5_25, dto.profitRate());
        assertEquals(DECIMAL_0_10, dto.vatRate());
        assertEquals(DECIMAL_0_0004, dto.takafulMonthlyRate());
        assertTrue(dto.active());
    }

    @Test
    void shouldPatchRequestEqualsAndHashCode() {
        PatchMourabahaParametersDTO d1 = new PatchMourabahaParametersDTO(DECIMAL_5_25, null, null, null);
        PatchMourabahaParametersDTO d2 = new PatchMourabahaParametersDTO(DECIMAL_5_25, null, null, null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldPatchRequestNotEqualToDifferentType() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO(null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldPatchRequestNotEqualWhenDifferentFields() {
        PatchMourabahaParametersDTO d1 = new PatchMourabahaParametersDTO(DECIMAL_5_25, null, null, null);
        PatchMourabahaParametersDTO d2 = new PatchMourabahaParametersDTO(DECIMAL_6_25, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldPatchRequestToString() {
        PatchMourabahaParametersDTO dto = new PatchMourabahaParametersDTO(DECIMAL_5_25, null, null, true);
        String str = dto.toString();
        assertTrue(str.contains(TEXT_5_25));
        assertTrue(str.contains("true"));
    }

    @Test
    void shouldSetAndGetResponseFields() {
        MourabahaParametersResponseDTO dto = new MourabahaParametersResponseDTO(
            ID_1, DECIMAL_5_25, DECIMAL_0_10, DECIMAL_0_0004, true);

        assertEquals(ID_1, dto.id());
        assertEquals(DECIMAL_5_25, dto.profitRate());
        assertEquals(DECIMAL_0_10, dto.vatRate());
        assertEquals(DECIMAL_0_0004, dto.takafulMonthlyRate());
        assertTrue(dto.active());
    }

    @Test
    void shouldResponseEqualsAndHashCode() {
        MourabahaParametersResponseDTO d1 = new MourabahaParametersResponseDTO(ID_1, null, null, null, null);
        MourabahaParametersResponseDTO d2 = new MourabahaParametersResponseDTO(ID_1, null, null, null, null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldResponseNotEqualToDifferentType() {
        MourabahaParametersResponseDTO dto = new MourabahaParametersResponseDTO(null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldResponseNotEqualWhenDifferentFields() {
        MourabahaParametersResponseDTO d1 = new MourabahaParametersResponseDTO(ID_1, null, null, null, null);
        MourabahaParametersResponseDTO d2 = new MourabahaParametersResponseDTO(ID_2, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldResponseToString() {
        MourabahaParametersResponseDTO dto = new MourabahaParametersResponseDTO(ID_1, null, null, null, true);
        String str = dto.toString();
        assertTrue(str.contains(ID_1.toString()));
        assertTrue(str.contains("true"));
    }
}
