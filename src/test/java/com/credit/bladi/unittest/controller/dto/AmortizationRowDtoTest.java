package com.credit.bladi.unittest.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.simulation.response.AmortizationRowDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AmortizationRowDtoTest {

    @Test
    void shouldSetAndGetAllFields() {
        AmortizationRowDTO dto = new AmortizationRowDTO();
        dto.setInstallmentNumber(1);
        dto.setBeginningBalance(new BigDecimal("500000.00"));
        dto.setInterest(new BigDecimal("2291.67"));
        dto.setMarginHt(new BigDecimal("1050.00"));
        dto.setVatOnMargin(new BigDecimal("105.00"));
        dto.setMarginTtc(new BigDecimal("1155.00"));
        dto.setPrincipal(new BigDecimal("1147.77"));
        dto.setPayment(new BigDecimal("3439.44"));
        dto.setEndingBalance(new BigDecimal("498852.23"));

        assertEquals(1, dto.getInstallmentNumber());
        assertEquals(new BigDecimal("500000.00"), dto.getBeginningBalance());
        assertEquals(new BigDecimal("2291.67"), dto.getInterest());
        assertEquals(new BigDecimal("1050.00"), dto.getMarginHt());
        assertEquals(new BigDecimal("105.00"), dto.getVatOnMargin());
        assertEquals(new BigDecimal("1155.00"), dto.getMarginTtc());
        assertEquals(new BigDecimal("1147.77"), dto.getPrincipal());
        assertEquals(new BigDecimal("3439.44"), dto.getPayment());
        assertEquals(new BigDecimal("498852.23"), dto.getEndingBalance());
    }

    @Test
    void shouldEqualsAndHashCode() {
        AmortizationRowDTO d1 = new AmortizationRowDTO();
        d1.setInstallmentNumber(1);

        AmortizationRowDTO d2 = new AmortizationRowDTO();
        d2.setInstallmentNumber(1);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldNotEqualWhenDifferentFields() {
        AmortizationRowDTO d1 = new AmortizationRowDTO();
        d1.setInstallmentNumber(1);

        AmortizationRowDTO d2 = new AmortizationRowDTO();
        d2.setInstallmentNumber(2);

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldNotEqualDifferentType() {
        assertFalse(new AmortizationRowDTO().equals("other"));
    }

    @Test
    void shouldToString() {
        AmortizationRowDTO dto = new AmortizationRowDTO();
        dto.setInstallmentNumber(1);
        dto.setPrincipal(new BigDecimal("1147.77"));
        String str = dto.toString();
        assertTrue(str.contains("1147.77"));
    }
}
