package com.credit.bladi.unittest.controller.mortgage.mapper;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_5000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_00;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MOURABAHA;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_NOPE;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.request;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.requestDTO;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.response;
import static com.credit.bladi.unittest.mock.mortgage.MockMortgageCredit.responseDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.credit.bladi.controller.dto.mortgage.request.MortgageCreditRequestDTO;
import com.credit.bladi.controller.dto.mortgage.response.MortgageCreditResponseDTO;
import com.credit.bladi.controller.mortgage.mapper.MortgageCreditMapper;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.util.List;
import org.junit.jupiter.api.Test;

class MortgageCreditMapperTest {
    @Test
    void shouldMapRequestDTOToDomain() {
        MortgageCreditRequest domain = MortgageCreditMapper.toDomain(requestDTO());

        assertEquals(request(), domain);
    }

    @Test
    void shouldMapResponseToDTO() {
        MortgageCreditResponseDTO dto = MortgageCreditMapper.toDTO(response());

        assertEquals(responseDTO(), dto);
    }

    @Test
    void shouldReturnNullWhenResponseIsNull() {
        assertNull(MortgageCreditMapper.toDTO(null));
    }

    @Test
    void shouldThrowCuratedValidationExceptionForUnknownCspCode() {
        MortgageCreditRequestDTO dto = withCspCode(requestDTO(), CODE_NOPE);

        ValidationException ex = assertThrows(ValidationException.class, () -> MortgageCreditMapper.toDomain(dto));
        assertEquals("validation.invalidValue", ex.getMessageKey());
        assertEquals("cspCode: NOPE", ex.getArgs()[0]);
    }

    @Test
    void shouldMapKnownProductTypeToDomain() {
        MortgageCreditRequestDTO dto = withProductType(requestDTO(), CODE_MOURABAHA);

        MortgageCreditRequest domain = MortgageCreditMapper.toDomain(dto);

        assertEquals(ProductType.MOURABAHA, domain.getProductType());
    }

    @Test
    void shouldThrowCuratedValidationExceptionForUnknownProductType() {
        MortgageCreditRequestDTO dto = withProductType(requestDTO(), CODE_NOPE);

        ValidationException ex = assertThrows(ValidationException.class, () -> MortgageCreditMapper.toDomain(dto));
        assertEquals("validation.invalidValue", ex.getMessageKey());
        assertEquals("productType: NOPE", ex.getArgs()[0]);
    }

    @Test
    void shouldMapConventionalAmortizationScheduleToDto() {
        Money m = Money.of(AMOUNT_1000);
        AmortizationRow row = new AmortizationRow(1, m, m, m, m, m);
        MortgageCreditResponse response = MortgageCreditResponse.getBuilder()
            .amount(m)
            .productType(ProductType.CONVENTIONAL)
            .cspCode(CspCode.SALA)
            .totalMonthlyIncome(m)
            .applicableRate(Rate.of(DECIMAL_5_00))
            .amortizationSchedule(List.of(row))
            .build();

        MortgageCreditResponseDTO dto = MortgageCreditMapper.toDTO(response);

        assertEquals(1, dto.amortizationSchedule().size());
        assertEquals(0, dto.amortizationSchedule().get(0).beginningBalance().compareTo(AMOUNT_1000));
    }

    @Test
    void shouldMapMourabahaSnapshotAndScheduleToDto() {
        Money m = Money.of(AMOUNT_1000);
        MourabahaFinancialSnapshot snapshot = new MourabahaFinancialSnapshot(m, m, m, m, m, m, m, m, m, m, m, m, m);
        MourabahaAmortizationRow row = new MourabahaAmortizationRow(1, m, m, m, m, m, m, m);
        MortgageCreditResponse response = MortgageCreditResponse.getBuilder()
            .amount(m)
            .productType(ProductType.MOURABAHA)
            .cspCode(CspCode.SALA)
            .totalMonthlyIncome(m)
            .applicableRate(Rate.of(DECIMAL_5_00))
            .mourabahaSnapshot(snapshot)
            .mourabahaAmortizationSchedule(List.of(row))
            .build();

        MortgageCreditResponseDTO dto = MortgageCreditMapper.toDTO(response);

        assertEquals(0, dto.mourabaha().monthlyPaymentTtc().compareTo(AMOUNT_1000));
        assertEquals(0, dto.mourabaha().marginHt().compareTo(AMOUNT_1000));
        assertEquals(0, dto.mourabaha().takafulMonthlyContribution().compareTo(AMOUNT_1000));
        assertEquals(0, dto.mourabaha().allInMonthlyPaymentTtc().compareTo(AMOUNT_1000));
        assertEquals(0, dto.mourabaha().totalTakafulCost().compareTo(AMOUNT_1000));
        assertEquals(1, dto.amortizationSchedule().size());
        assertEquals(0, dto.amortizationSchedule().get(0).marginHt().compareTo(AMOUNT_1000));
    }

    @Test
    void shouldMapCoBorrowerMonthlyIncomeWhenPresent() {
        MortgageCreditRequestDTO base = requestDTO();
        MortgageCreditRequestDTO dto = new MortgageCreditRequestDTO(base.applicantName(), base.email(), base.phone(),
            base.amount(), base.duration(), base.cspCode(),
            base.productType(), base.nationalityCode(), base.moroccanResident(), base.propertyValue(),
            base.monthlyIncome(), base.age(), base.coBorrower(), AMOUNT_5000);

        com.credit.bladi.model.mortgage.request.MortgageCreditRequest domain = MortgageCreditMapper.toDomain(dto);

        assertEquals(0, domain.getCoBorrowerMonthlyIncome().amount().compareTo(AMOUNT_5000));
    }

    private static MortgageCreditRequestDTO withCspCode(MortgageCreditRequestDTO base, String cspCode) {
        return new MortgageCreditRequestDTO(base.applicantName(), base.email(), base.phone(), base.amount(),
            base.duration(), cspCode, base.productType(),
            base.nationalityCode(), base.moroccanResident(), base.propertyValue(), base.monthlyIncome(),
            base.age(), base.coBorrower(), base.coBorrowerMonthlyIncome());
    }

    private static MortgageCreditRequestDTO withProductType(MortgageCreditRequestDTO base, String productType) {
        return new MortgageCreditRequestDTO(base.applicantName(), base.email(), base.phone(), base.amount(),
            base.duration(), base.cspCode(), productType,
            base.nationalityCode(), base.moroccanResident(), base.propertyValue(), base.monthlyIncome(),
            base.age(), base.coBorrower(), base.coBorrowerMonthlyIncome());
    }
}
