package com.credit.bladi.controller.simulation.mapper;

import com.credit.bladi.controller.dto.simulation.request.SimulationRequestDTO;
import com.credit.bladi.controller.dto.simulation.response.AmortizationRowDTO;
import com.credit.bladi.controller.dto.simulation.response.SimulationResponseDTO;
import com.credit.bladi.exception.ValidationException;
import com.credit.bladi.exception.ValidationException.ValidationExceptionType;
import com.credit.bladi.model.enums.CspCode;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.simulation.request.SimulationRequest;
import com.credit.bladi.model.simulation.response.SimulationResponse;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import java.util.List;

public final class SimulationMapper {

    private SimulationMapper() {
    }

    public static SimulationRequest toDomain(SimulationRequestDTO simulationRequestDTO) {
        return SimulationRequest.getBuilder()
            .amount(Money.of(simulationRequestDTO.getAmount()))
            .duration(simulationRequestDTO.getDuration())
            .productType(parseProductType(simulationRequestDTO.getProductType()))
            .cspCode(parseCspCode(simulationRequestDTO.getCspCode()))
            .nationalityCode(simulationRequestDTO.getNationalityCode())
            .moroccanResident(simulationRequestDTO.getMoroccanResident())
            .propertyValue(Money.of(simulationRequestDTO.getPropertyValue()))
            .monthlyIncome(Money.of(simulationRequestDTO.getMonthlyIncome()))
            .age(simulationRequestDTO.getAge())
            .coBorrower(simulationRequestDTO.getCoBorrower())
            .coBorrowerMonthlyIncome(
                simulationRequestDTO.getCoBorrowerMonthlyIncome() != null
                    ? Money.of(simulationRequestDTO.getCoBorrowerMonthlyIncome()) : null)
            .build();
    }

    private static CspCode parseCspCode(String value) {
        try {
            return CspCode.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw ValidationException.of(ValidationExceptionType.INVALID_VALUE, "cspCode: " + value);
        }
    }

    private static ProductType parseProductType(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ProductType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw ValidationException.of(ValidationExceptionType.INVALID_VALUE, "productType: " + value);
        }
    }

    public static SimulationResponseDTO toDTO(SimulationResponse simulationResponse) {
        if (simulationResponse == null) {
            return null;
        }
        SimulationResponseDTO dto = new SimulationResponseDTO();
        dto.setAmount(simulationResponse.getAmount().amount());
        dto.setDuration(simulationResponse.getDuration());
        dto.setCspCode(simulationResponse.getCspCode().name());
        dto.setProductType(simulationResponse.getProductType().name());
        dto.setNationalityCode(simulationResponse.getNationalityCode());
        dto.setMoroccanResident(simulationResponse.getMoroccanResident());
        dto.setAge(simulationResponse.getAge());
        dto.setTotalMonthlyIncome(simulationResponse.getTotalMonthlyIncome().amount());
        dto.setApplicableRate(simulationResponse.getApplicableRate().value());
        dto.setTaeg(simulationResponse.getTaeg());

        FinancialSnapshot applicable = simulationResponse.getApplicable();
        if (applicable != null) {
            fillConventional(dto, applicable, simulationResponse.getRateMin(), simulationResponse.getRateMax(),
                simulationResponse.getBounds());
        }

        MourabahaFinancialSnapshot mourabahaSnapshot = simulationResponse.getMourabahaSnapshot();
        if (mourabahaSnapshot != null) {
            fillMourabaha(dto, mourabahaSnapshot);
        }

        List<AmortizationRow> schedule = simulationResponse.getAmortizationSchedule();
        if (schedule != null) {
            dto.setAmortizationSchedule(schedule.stream().map(SimulationMapper::toRowDTO).toList());
        }
        List<MourabahaAmortizationRow> mourabahaSchedule = simulationResponse.getMourabahaAmortizationSchedule();
        if (mourabahaSchedule != null) {
            dto.setAmortizationSchedule(mourabahaSchedule.stream().map(SimulationMapper::toRowDTO).toList());
        }
        return dto;
    }

    private static void fillConventional(SimulationResponseDTO dto, FinancialSnapshot applicable,
                                         Rate rateMin, Rate rateMax, RateBoundsSnapshot bounds) {
        dto.setRateMin(rateMin.value());
        dto.setRateMax(rateMax.value());
        dto.setMonthlyPayment(applicable.monthlyPayment().amount());
        dto.setAdiMonthlyPremium(applicable.adiMonthlyPremium().amount());
        dto.setAllInMonthlyPayment(applicable.allInMonthlyPayment().amount());
        dto.setTotalInterest(applicable.totalInterest().amount());
        dto.setTotalAdiCost(applicable.totalAdiCost().amount());
        dto.setTotalCost(applicable.totalCost().amount());
        dto.setMonthlyPaymentMin(bounds.min().monthlyPayment().amount());
        dto.setMonthlyPaymentMax(bounds.max().monthlyPayment().amount());
        dto.setAdiMonthlyPremiumMin(bounds.min().adiMonthlyPremium().amount());
        dto.setAdiMonthlyPremiumMax(bounds.max().adiMonthlyPremium().amount());
        dto.setAllInMonthlyPaymentMin(bounds.min().allInMonthlyPayment().amount());
        dto.setAllInMonthlyPaymentMax(bounds.max().allInMonthlyPayment().amount());
        dto.setTotalInterestMin(bounds.min().totalInterest().amount());
        dto.setTotalInterestMax(bounds.max().totalInterest().amount());
        dto.setTotalAdiCostMin(bounds.min().totalAdiCost().amount());
        dto.setTotalAdiCostMax(bounds.max().totalAdiCost().amount());
        dto.setTotalCostMin(bounds.min().totalCost().amount());
        dto.setTotalCostMax(bounds.max().totalCost().amount());
        dto.setRegistrationFees(applicable.registrationFees().amount());
        dto.setLandConservationFees(applicable.landConservationFees().amount());
        dto.setNotaryFees(applicable.notaryFees().amount());
        dto.setProcessingFees(applicable.processingFees().amount());
        dto.setTotalFees(applicable.totalFees().amount());
    }

    private static void fillMourabaha(SimulationResponseDTO dto, MourabahaFinancialSnapshot snapshot) {
        dto.setMonthlyPaymentTtc(snapshot.monthlyPaymentTtc().amount());
        dto.setMarginHt(snapshot.marginHt().amount());
        dto.setVatOnMargin(snapshot.vatOnMargin().amount());
        dto.setMarginTtc(snapshot.marginTtc().amount());
        dto.setTotalCost(snapshot.totalCostTtc().amount());
        dto.setRegistrationFees(snapshot.registrationFees().amount());
        dto.setLandConservationFees(snapshot.landConservationFees().amount());
        dto.setNotaryFees(snapshot.notaryFees().amount());
        dto.setProcessingFees(snapshot.processingFees().amount());
        dto.setTotalFees(snapshot.totalFees().amount());
    }

    private static AmortizationRowDTO toRowDTO(AmortizationRow row) {
        AmortizationRowDTO rowDto = new AmortizationRowDTO();
        rowDto.setInstallmentNumber(row.installmentNumber());
        rowDto.setBeginningBalance(row.beginningBalance().amount());
        rowDto.setInterest(row.interest().amount());
        rowDto.setPrincipal(row.principal().amount());
        rowDto.setPayment(row.payment().amount());
        rowDto.setEndingBalance(row.endingBalance().amount());
        return rowDto;
    }

    private static AmortizationRowDTO toRowDTO(MourabahaAmortizationRow row) {
        AmortizationRowDTO rowDto = new AmortizationRowDTO();
        rowDto.setInstallmentNumber(row.installmentNumber());
        rowDto.setBeginningBalance(row.beginningBalance().amount());
        rowDto.setMarginHt(row.marginHt().amount());
        rowDto.setVatOnMargin(row.vatOnMargin().amount());
        rowDto.setMarginTtc(row.marginTtc().amount());
        rowDto.setPrincipal(row.principal().amount());
        rowDto.setPayment(row.payment().amount());
        rowDto.setEndingBalance(row.endingBalance().amount());
        return rowDto;
    }
}
