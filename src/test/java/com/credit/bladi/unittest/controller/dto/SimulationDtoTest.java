package com.credit.bladi.unittest.controller.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.controller.dto.simulation.request.SimulationRequestDTO;
import com.credit.bladi.controller.dto.simulation.response.AmortizationRowDTO;
import com.credit.bladi.controller.dto.simulation.response.SimulationResponseDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SimulationDtoTest {

    @Test
    void shouldSetAndGetSimulationRequestFields() {
        SimulationRequestDTO dto = new SimulationRequestDTO();
        dto.setAmount(new BigDecimal("500000"));
        dto.setDuration(240);
        dto.setCspCode("SALA");
        dto.setNationalityCode("MA");
        dto.setMoroccanResident(true);
        dto.setAge(40);
        dto.setCoBorrower(true);

        assertEquals(new BigDecimal("500000"), dto.getAmount());
        assertEquals(240, dto.getDuration());
        assertEquals("SALA", dto.getCspCode());
        assertEquals("MA", dto.getNationalityCode());
        assertTrue(dto.getMoroccanResident());
        assertEquals(40, dto.getAge());
        assertTrue(dto.getCoBorrower());
    }

    @Test
    void shouldSimulationRequestEqualsAndHashCode() {
        SimulationRequestDTO d1 = new SimulationRequestDTO();
        d1.setAmount(new BigDecimal("500000"));
        d1.setDuration(240);

        SimulationRequestDTO d2 = new SimulationRequestDTO();
        d2.setAmount(new BigDecimal("500000"));
        d2.setDuration(240);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldSimulationRequestNotEqualWhenDifferentFields() {
        SimulationRequestDTO d1 = new SimulationRequestDTO();
        d1.setAmount(new BigDecimal("500000"));

        SimulationRequestDTO d2 = new SimulationRequestDTO();
        d2.setAmount(new BigDecimal("600000"));

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldSimulationRequestNotEqualToDifferentType() {
        SimulationRequestDTO dto = new SimulationRequestDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldSimulationRequestToString() {
        SimulationRequestDTO dto = new SimulationRequestDTO();
        dto.setAmount(new BigDecimal("500000"));
        dto.setCspCode("SALA");
        String str = dto.toString();
        assertTrue(str.contains("SALA"));
    }

    @Test
    void shouldSetAndGetSimulationResponseFields() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setAmount(new BigDecimal("500000"));
        dto.setDuration(240);
        dto.setCspCode("SALA");
        dto.setProductType("CONVENTIONAL");
        dto.setNationalityCode("MA");
        dto.setMoroccanResident(true);
        dto.setAge(40);
        dto.setTotalMonthlyIncome(new BigDecimal("20000"));
        dto.setApplicableRate(new BigDecimal("4.80"));
        dto.setRateMin(new BigDecimal("3.50"));
        dto.setRateMax(new BigDecimal("5.50"));
        dto.setMonthlyPayment(new BigDecimal("3000"));
        dto.setAdiMonthlyPremium(new BigDecimal("100"));
        dto.setAllInMonthlyPayment(new BigDecimal("3100"));
        dto.setTotalInterest(new BigDecimal("220000"));
        dto.setTotalAdiCost(new BigDecimal("24000"));
        dto.setTotalCost(new BigDecimal("744000"));
        dto.setTaeg(new BigDecimal("5.10"));
        dto.setMonthlyPaymentMin(new BigDecimal("2800"));
        dto.setMonthlyPaymentMax(new BigDecimal("3200"));
        dto.setAdiMonthlyPremiumMin(new BigDecimal("90"));
        dto.setAdiMonthlyPremiumMax(new BigDecimal("110"));
        dto.setAllInMonthlyPaymentMin(new BigDecimal("2890"));
        dto.setAllInMonthlyPaymentMax(new BigDecimal("3310"));
        dto.setTotalInterestMin(new BigDecimal("72000"));
        dto.setTotalInterestMax(new BigDecimal("168000"));
        dto.setTotalAdiCostMin(new BigDecimal("20000"));
        dto.setTotalAdiCostMax(new BigDecimal("28000"));
        dto.setTotalCostMin(new BigDecimal("572000"));
        dto.setTotalCostMax(new BigDecimal("668000"));
        dto.setRegistrationFees(new BigDecimal("7500"));
        dto.setLandConservationFees(new BigDecimal("7500"));
        dto.setNotaryFees(new BigDecimal("5000"));
        dto.setProcessingFees(new BigDecimal("5000"));
        dto.setTotalFees(new BigDecimal("25000"));
        dto.setVatOnMargin(new BigDecimal("105"));
        dto.setMarginTtc(new BigDecimal("1155"));
        dto.setMonthlyPaymentTtc(new BigDecimal("3439"));

        assertEquals(new BigDecimal("500000"), dto.getAmount());
        assertEquals(240, dto.getDuration());
        assertEquals("SALA", dto.getCspCode());
        assertEquals("CONVENTIONAL", dto.getProductType());
        assertEquals("MA", dto.getNationalityCode());
        assertTrue(dto.getMoroccanResident());
        assertEquals(40, dto.getAge());
        assertEquals(new BigDecimal("20000"), dto.getTotalMonthlyIncome());
        assertEquals(new BigDecimal("4.80"), dto.getApplicableRate());
        assertEquals(new BigDecimal("3.50"), dto.getRateMin());
        assertEquals(new BigDecimal("5.50"), dto.getRateMax());
        assertEquals(new BigDecimal("3000"), dto.getMonthlyPayment());
        assertEquals(new BigDecimal("100"), dto.getAdiMonthlyPremium());
        assertEquals(new BigDecimal("3100"), dto.getAllInMonthlyPayment());
        assertEquals(new BigDecimal("220000"), dto.getTotalInterest());
        assertEquals(new BigDecimal("24000"), dto.getTotalAdiCost());
        assertEquals(new BigDecimal("744000"), dto.getTotalCost());
        assertEquals(new BigDecimal("5.10"), dto.getTaeg());
        assertEquals(new BigDecimal("2800"), dto.getMonthlyPaymentMin());
        assertEquals(new BigDecimal("3200"), dto.getMonthlyPaymentMax());
        assertEquals(new BigDecimal("90"), dto.getAdiMonthlyPremiumMin());
        assertEquals(new BigDecimal("110"), dto.getAdiMonthlyPremiumMax());
        assertEquals(new BigDecimal("2890"), dto.getAllInMonthlyPaymentMin());
        assertEquals(new BigDecimal("3310"), dto.getAllInMonthlyPaymentMax());
        assertEquals(new BigDecimal("72000"), dto.getTotalInterestMin());
        assertEquals(new BigDecimal("168000"), dto.getTotalInterestMax());
        assertEquals(new BigDecimal("20000"), dto.getTotalAdiCostMin());
        assertEquals(new BigDecimal("28000"), dto.getTotalAdiCostMax());
        assertEquals(new BigDecimal("572000"), dto.getTotalCostMin());
        assertEquals(new BigDecimal("668000"), dto.getTotalCostMax());
        assertEquals(new BigDecimal("7500"), dto.getRegistrationFees());
        assertEquals(new BigDecimal("7500"), dto.getLandConservationFees());
        assertEquals(new BigDecimal("5000"), dto.getNotaryFees());
        assertEquals(new BigDecimal("5000"), dto.getProcessingFees());
        assertEquals(new BigDecimal("25000"), dto.getTotalFees());
        assertEquals(new BigDecimal("105"), dto.getVatOnMargin());
        assertEquals(new BigDecimal("1155"), dto.getMarginTtc());
        assertEquals(new BigDecimal("3439"), dto.getMonthlyPaymentTtc());
    }

    @Test
    void shouldSimulationResponseEqualsAndHashCode() {
        SimulationResponseDTO d1 = new SimulationResponseDTO();
        d1.setAmount(new BigDecimal("500000"));
        d1.setDuration(240);

        SimulationResponseDTO d2 = new SimulationResponseDTO();
        d2.setAmount(new BigDecimal("500000"));
        d2.setDuration(240);

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void shouldSimulationResponseNotEqualWhenDifferentFields() {
        SimulationResponseDTO d1 = new SimulationResponseDTO();
        d1.setAmount(new BigDecimal("500000"));

        SimulationResponseDTO d2 = new SimulationResponseDTO();
        d2.setAmount(new BigDecimal("600000"));

        assertFalse(d1.equals(d2));
    }

    @Test
    void shouldSimulationResponseNotEqualToDifferentType() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        assertFalse(dto.equals("other"));
    }

    @Test
    void shouldSimulationResponseToStringWithAmortizationSchedule() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setAmortizationSchedule(java.util.List.of(new AmortizationRowDTO()));
        String str = dto.toString();
        assertTrue(str.contains("amortizationScheduleSize=1"));
    }

    @Test
    void shouldSimulationResponseToString() {
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setAmount(new BigDecimal("500000"));
        dto.setCspCode("SALA");
        String str = dto.toString();
        assertTrue(str.contains("500000"));
        assertTrue(str.contains("SALA"));
    }
}