package com.credit.bladi.unittest.controller.simulation.mapper;

import static com.credit.bladi.unittest.mock.simulation.MockSimulation.request;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.requestDTO;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.response;
import static com.credit.bladi.unittest.mock.simulation.MockSimulation.responseDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.controller.dto.simulation.request.SimulationRequestDTO;
import com.credit.bladi.controller.dto.simulation.response.SimulationResponseDTO;
import com.credit.bladi.controller.simulation.mapper.SimulationMapper;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.simulation.response.SimulationResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimulationMapperTest {

    @Test
    void shouldMapRequestDTOToDomain() {
        SimulationRequest domain = SimulationMapper.toDomain(requestDTO());

        assertEquals(request(), domain);
    }

    @Test
    void shouldMapResponseToDTO() {
        SimulationResponseDTO dto = SimulationMapper.toDTO(response());

        assertEquals(responseDTO(), dto);
    }

    @Test
    void shouldReturnNullWhenResponseIsNull() {
        assertNull(SimulationMapper.toDTO(null));
    }

    @Test
    void shouldThrowCuratedValidationExceptionForUnknownCspCode() {
        SimulationRequestDTO dto = requestDTO();
        dto.setCspCode("NOPE");

        ValidationException ex = assertThrows(ValidationException.class, () -> SimulationMapper.toDomain(dto));
        assertEquals("INVALID_VALUE: cspCode: NOPE", ex.getMessage());
    }

    @Test
    void shouldMapKnownProductTypeToDomain() {
        SimulationRequestDTO dto = requestDTO();
        dto.setProductType("MOURABAHA");

        SimulationRequest domain = SimulationMapper.toDomain(dto);

        assertEquals(ProductType.MOURABAHA, domain.getProductType());
    }

    @Test
    void shouldThrowCuratedValidationExceptionForUnknownProductType() {
        SimulationRequestDTO dto = requestDTO();
        dto.setProductType("NOPE");

        ValidationException ex = assertThrows(ValidationException.class, () -> SimulationMapper.toDomain(dto));
        assertEquals("INVALID_VALUE: productType: NOPE", ex.getMessage());
    }

    @Test
    void shouldMapConventionalAmortizationScheduleToDto() {
        Money m = Money.of(new BigDecimal("1000"));
        AmortizationRow row = new AmortizationRow(1, m, m, m, m, m);
        SimulationResponse response = SimulationResponse.getBuilder()
            .amount(m)
            .productType(ProductType.CONVENTIONAL)
            .cspCode(CspCode.SALA)
            .totalMonthlyIncome(m)
            .applicableRate(Rate.of(new BigDecimal("5.00")))
            .amortizationSchedule(List.of(row))
            .build();

        SimulationResponseDTO dto = SimulationMapper.toDTO(response);

        assertEquals(1, dto.getAmortizationSchedule().size());
        assertEquals(0, dto.getAmortizationSchedule().get(0).getBeginningBalance().compareTo(new BigDecimal("1000")));
    }

    @Test
    void shouldMapMourabahaSnapshotAndScheduleToDto() {
        Money m = Money.of(new BigDecimal("1000"));
        MourabahaFinancialSnapshot snapshot = new MourabahaFinancialSnapshot(m, m, m, m, m, m, m, m, m, m);
        MourabahaAmortizationRow row = new MourabahaAmortizationRow(1, m, m, m, m, m, m, m);
        SimulationResponse response = SimulationResponse.getBuilder()
            .amount(m)
            .productType(ProductType.MOURABAHA)
            .cspCode(CspCode.SALA)
            .totalMonthlyIncome(m)
            .applicableRate(Rate.of(new BigDecimal("5.00")))
            .mourabahaSnapshot(snapshot)
            .mourabahaAmortizationSchedule(List.of(row))
            .build();

        SimulationResponseDTO dto = SimulationMapper.toDTO(response);

        assertEquals(0, dto.getMonthlyPaymentTtc().compareTo(new BigDecimal("1000")));
        assertEquals(0, dto.getMarginHt().compareTo(new BigDecimal("1000")));
        assertEquals(1, dto.getAmortizationSchedule().size());
        assertEquals(0, dto.getAmortizationSchedule().get(0).getMarginHt().compareTo(new BigDecimal("1000")));
    }

    @Test
    void shouldMapCoBorrowerMonthlyIncomeWhenPresent() {
        SimulationRequestDTO dto = requestDTO();
        dto.setCoBorrowerMonthlyIncome(new java.math.BigDecimal("5000"));

        com.credit.bladi.model.simulation.request.SimulationRequest domain = SimulationMapper.toDomain(dto);

        assertEquals(0, domain.getCoBorrowerMonthlyIncome().amount().compareTo(new java.math.BigDecimal("5000")));
    }
}