package com.credit.bladi.unittest.controller.dto;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1050_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_105_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1147_77;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_1155_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_2291_67;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_3439_44;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_498852_23;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_500000_00;
import static com.credit.bladi.unittest.mock.data.MockAmounts.TEXT_1147_77;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.AmortizationRowDTO;
import org.junit.jupiter.api.Test;

class AmortizationRowDtoTest {
    @Test
    void shouldSetAndGetAllFields() {
        AmortizationRowDTO dto = new AmortizationRowDTO(
            1,
            DECIMAL_500000_00,
            DECIMAL_2291_67,
            DECIMAL_1050_00,
            DECIMAL_105_00,
            DECIMAL_1155_00,
            DECIMAL_1147_77,
            DECIMAL_3439_44,
            DECIMAL_498852_23);

        assertEquals(1, dto.installmentNumber());
        assertEquals(DECIMAL_500000_00, dto.beginningBalance());
        assertEquals(DECIMAL_2291_67, dto.interest());
        assertEquals(DECIMAL_1050_00, dto.marginHt());
        assertEquals(DECIMAL_105_00, dto.vatOnMargin());
        assertEquals(DECIMAL_1155_00, dto.marginTtc());
        assertEquals(DECIMAL_1147_77, dto.principal());
        assertEquals(DECIMAL_3439_44, dto.payment());
        assertEquals(DECIMAL_498852_23, dto.endingBalance());
    }

    @Test
    void shouldEqualsAndHashCode() {
        AmortizationRowDTO d1 = new AmortizationRowDTO(1, null, null, null, null, null, null, null, null);
        AmortizationRowDTO d2 = new AmortizationRowDTO(1, null, null, null, null, null, null, null, null);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldNotEqualWhenDifferentFields() {
        AmortizationRowDTO d1 = new AmortizationRowDTO(1, null, null, null, null, null, null, null, null);
        AmortizationRowDTO d2 = new AmortizationRowDTO(2, null, null, null, null, null, null, null, null);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldNotEqualDifferentType() {
        AmortizationRowDTO dto = new AmortizationRowDTO(1, null, null, null, null, null, null, null, null);
        assertFalse(dto.equals(USER_OTHER));
    }

    @Test
    void shouldToString() {
        AmortizationRowDTO dto = new AmortizationRowDTO(
            1, null, null, null, null, null, DECIMAL_1147_77, null, null);
        String str = dto.toString();
        assertTrue(str.contains(TEXT_1147_77));
    }
}
