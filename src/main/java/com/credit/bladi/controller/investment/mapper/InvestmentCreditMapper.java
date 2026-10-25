package com.credit.bladi.controller.investment.mapper;

import com.credit.bladi.common.EnumParsers;
import com.credit.bladi.controller.dto.AmortizationRowDTO;
import com.credit.bladi.controller.dto.investment.request.InvestmentCreditRequestDTO;
import com.credit.bladi.controller.dto.investment.response.InvestmentCreditResponseDTO;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.investment.request.InvestmentCreditRequest;
import com.credit.bladi.model.investment.response.InvestmentCreditResponse;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;

public final class InvestmentCreditMapper {
    private InvestmentCreditMapper() {
    }

    public static InvestmentCreditRequest toDomain(InvestmentCreditRequestDTO dto) {
        return InvestmentCreditRequest.getBuilder()
            .companyName(dto.companyName())
            .enterpriseType(EnumParsers.parse(EnterpriseType.class, dto.enterpriseType(), "enterpriseType"))
            .sector(dto.sector())
            .email(dto.email())
            .phone(dto.phone())
            .amount(Money.of(dto.amount()))
            .duration(dto.duration())
            .annualRevenue(Money.of(dto.annualRevenue()))
            .companyAgeYears(dto.companyAgeYears())
            .existingAnnualDebtService(
                dto.existingAnnualDebtService() != null ? Money.of(dto.existingAnnualDebtService()) : null)
            .netAnnualResult(Money.of(dto.netAnnualResult()))
            .guaranteesValue(Money.of(dto.guaranteesValue()))
            .build();
    }

    public static InvestmentCreditResponseDTO toDTO(InvestmentCreditResponse response) {
        if (response == null) {
            return null;
        }
        InvestmentFinancialSnapshot snapshot = response.snapshot();
        return new InvestmentCreditResponseDTO(
            response.companyName(),
            response.enterpriseType().name(),
            response.sector(),
            response.amount().amount(),
            response.duration(),
            snapshot.applicableRate().value(),
            snapshot.monthlyPayment().amount(),
            snapshot.annualDebtService().amount(),
            snapshot.totalInterest().amount(),
            snapshot.totalCost().amount(),
            snapshot.processingFees().amount(),
            snapshot.taeg(),
            response.demandReference(),
            snapshot.amortizationSchedule().stream().map(InvestmentCreditMapper::toRowDTO).toList());
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
}
