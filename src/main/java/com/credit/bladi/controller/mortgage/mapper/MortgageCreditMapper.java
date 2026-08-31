package com.credit.bladi.controller.mortgage.mapper;

import com.credit.bladi.common.EnumParsers;
import com.credit.bladi.controller.dto.AmortizationRowDTO;
import com.credit.bladi.controller.dto.mortgage.request.MortgageCreditRequestDTO;
import com.credit.bladi.controller.dto.mortgage.response.ConventionalFinancingResponseDTO;
import com.credit.bladi.controller.dto.mortgage.response.MortgageCreditResponseDTO;
import com.credit.bladi.controller.dto.mortgage.response.MourabahaFinancingResponseDTO;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.mortgage.response.MortgageCreditResponse;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import java.math.BigDecimal;
import java.util.List;

public final class MortgageCreditMapper {
    private MortgageCreditMapper() {
    }

    public static MortgageCreditRequest toDomain(MortgageCreditRequestDTO simulationRequestDTO) {
        return MortgageCreditRequest.getBuilder()
            .applicantName(simulationRequestDTO.applicantName())
            .email(simulationRequestDTO.email())
            .phone(simulationRequestDTO.phone())
            .amount(Money.of(simulationRequestDTO.amount()))
            .duration(simulationRequestDTO.duration())
            .productType(EnumParsers.parseNullable(ProductType.class, simulationRequestDTO.productType(),
                "productType"))
            .cspCode(EnumParsers.parse(CspCode.class, simulationRequestDTO.cspCode(), "cspCode"))
            .nationalityCode(simulationRequestDTO.nationalityCode())
            .moroccanResident(simulationRequestDTO.moroccanResident())
            .propertyValue(Money.of(simulationRequestDTO.propertyValue()))
            .monthlyIncome(Money.of(simulationRequestDTO.monthlyIncome()))
            .age(simulationRequestDTO.age())
            .coBorrower(simulationRequestDTO.coBorrower())
            .coBorrowerMonthlyIncome(
                simulationRequestDTO.coBorrowerMonthlyIncome() != null
                    ? Money.of(simulationRequestDTO.coBorrowerMonthlyIncome()) : null)
            .build();
    }

    public static MortgageCreditResponseDTO toDTO(MortgageCreditResponse simulationResponse) {
        if (simulationResponse == null) {
            return null;
        }
        Fees fees = feesOf(simulationResponse);
        return new MortgageCreditResponseDTO(
            simulationResponse.amount().amount(),
            simulationResponse.duration(),
            simulationResponse.cspCode().name(),
            simulationResponse.productType().name(),
            simulationResponse.nationalityCode(),
            simulationResponse.moroccanResident(),
            simulationResponse.age(),
            simulationResponse.totalMonthlyIncome().amount(),
            simulationResponse.applicableRate().value(),
            simulationResponse.taeg(),
            fees.registration(),
            fees.landConservation(),
            fees.notary(),
            fees.processing(),
            fees.total(),
            toConventionalDTO(simulationResponse),
            toMourabahaDTO(simulationResponse.mourabahaSnapshot()),
            scheduleOf(simulationResponse),
            simulationResponse.demandReference());
    }

    private record Fees(BigDecimal registration, BigDecimal landConservation, BigDecimal notary,
                        BigDecimal processing, BigDecimal total) {
        private static final Fees NONE = new Fees(null, null, null, null, null);
    }

    private static Fees feesOf(MortgageCreditResponse simulationResponse) {
        MourabahaFinancialSnapshot mourabaha = simulationResponse.mourabahaSnapshot();
        if (mourabaha != null) {
            return new Fees(mourabaha.registrationFees().amount(), mourabaha.landConservationFees().amount(),
                mourabaha.notaryFees().amount(), mourabaha.processingFees().amount(),
                mourabaha.totalFees().amount());
        }
        FinancialSnapshot applicable = simulationResponse.applicable();
        if (applicable != null) {
            return new Fees(applicable.registrationFees().amount(), applicable.landConservationFees().amount(),
                applicable.notaryFees().amount(), applicable.processingFees().amount(),
                applicable.totalFees().amount());
        }
        return Fees.NONE;
    }

    private static ConventionalFinancingResponseDTO toConventionalDTO(MortgageCreditResponse simulationResponse) {
        FinancialSnapshot applicable = simulationResponse.applicable();
        if (applicable == null) {
            return null;
        }
        RateBoundsSnapshot bounds = simulationResponse.bounds();
        return new ConventionalFinancingResponseDTO(
            simulationResponse.rateMin().value(),
            simulationResponse.rateMax().value(),
            applicable.monthlyPayment().amount(),
            applicable.adiMonthlyPremium().amount(),
            applicable.allInMonthlyPayment().amount(),
            applicable.totalInterest().amount(),
            applicable.totalAdiCost().amount(),
            applicable.totalCost().amount(),
            bounds.min().monthlyPayment().amount(),
            bounds.max().monthlyPayment().amount(),
            bounds.min().adiMonthlyPremium().amount(),
            bounds.max().adiMonthlyPremium().amount(),
            bounds.min().allInMonthlyPayment().amount(),
            bounds.max().allInMonthlyPayment().amount(),
            bounds.min().totalInterest().amount(),
            bounds.max().totalInterest().amount(),
            bounds.min().totalAdiCost().amount(),
            bounds.max().totalAdiCost().amount(),
            bounds.min().totalCost().amount(),
            bounds.max().totalCost().amount());
    }

    private static MourabahaFinancingResponseDTO toMourabahaDTO(MourabahaFinancialSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        return new MourabahaFinancingResponseDTO(
            snapshot.marginHt().amount(),
            snapshot.vatOnMargin().amount(),
            snapshot.marginTtc().amount(),
            snapshot.monthlyPaymentTtc().amount(),
            snapshot.takafulMonthlyContribution().amount(),
            snapshot.allInMonthlyPaymentTtc().amount(),
            snapshot.totalTakafulCost().amount(),
            snapshot.totalCostTtc().amount());
    }

    private static List<AmortizationRowDTO> scheduleOf(MortgageCreditResponse simulationResponse) {
        List<MourabahaAmortizationRow> mourabahaSchedule = simulationResponse.mourabahaAmortizationSchedule();
        if (mourabahaSchedule != null) {
            return mourabahaSchedule.stream().map(MortgageCreditMapper::toRowDTO).toList();
        }
        List<AmortizationRow> schedule = simulationResponse.amortizationSchedule();
        if (schedule != null) {
            return schedule.stream().map(MortgageCreditMapper::toRowDTO).toList();
        }
        return null;
    }

    public static AmortizationRowDTO toRowDTO(AmortizationRow row) {
        return new AmortizationRowDTO(
            row.installmentNumber(),
            row.beginningBalance().amount(),
            row.interest().amount(),
            null,
            null,
            null,
            row.principal().amount(),
            row.payment().amount(),
            row.endingBalance().amount());
    }

    public static AmortizationRowDTO toRowDTO(MourabahaAmortizationRow row) {
        return new AmortizationRowDTO(
            row.installmentNumber(),
            row.beginningBalance().amount(),
            null,
            row.marginHt().amount(),
            row.vatOnMargin().amount(),
            row.marginTtc().amount(),
            row.principal().amount(),
            row.payment().amount(),
            row.endingBalance().amount());
    }
}
