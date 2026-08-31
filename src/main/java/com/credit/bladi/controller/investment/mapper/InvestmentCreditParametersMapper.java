package com.credit.bladi.controller.investment.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRate;
import static com.credit.bladi.model.vo.ValueObjectMappers.toRatio;

import com.credit.bladi.controller.dto.investment.request.PatchInvestmentCreditParametersDTO;
import com.credit.bladi.controller.dto.investment.response.InvestmentCreditParametersResponseDTO;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;

public final class InvestmentCreditParametersMapper {
    private InvestmentCreditParametersMapper() {
    }

    public static InvestmentCreditParametersModel toDomain(Long id, PatchInvestmentCreditParametersDTO dto) {
        return InvestmentCreditParametersModel.getBuilder()
            .id(id)
            .rateMin(toRate(dto.rateMin()))
            .rateMax(toRate(dto.rateMax()))
            .rateFormula(toRateFormula(dto.rateFormula()))
            .minCompanyAgeYears(dto.minCompanyAgeYears())
            .maxFinancingToRevenueRatio(toRatio(dto.maxFinancingToRevenueRatio()))
            .minGuaranteeCoverageRatio(toRatio(dto.minGuaranteeCoverageRatio()))
            .maxDebtServiceRatio(toRatio(dto.maxDebtServiceRatio()))
            .processingFeesRatio(toRatio(dto.processingFeesRatio()))
            .insurancePremiumRatio(toRatio(dto.insurancePremiumRatio()))
            .active(dto.active())
            .build();
    }

    public static InvestmentCreditParametersResponseDTO toResponseDTO(InvestmentCreditParametersModel model) {
        if (model == null) {
            return null;
        }
        return new InvestmentCreditParametersResponseDTO(
            model.getPublicId(),
            model.getEnterpriseType() != null ? model.getEnterpriseType().name() : null,
            toBigDecimal(model.getRateMin()),
            toBigDecimal(model.getRateMax()),
            model.getRateFormula() != null ? model.getRateFormula().name() : null,
            model.getMinCompanyAgeYears(),
            toBigDecimal(model.getMaxFinancingToRevenueRatio()),
            toBigDecimal(model.getMinGuaranteeCoverageRatio()),
            toBigDecimal(model.getMaxDebtServiceRatio()),
            toBigDecimal(model.getProcessingFeesRatio()),
            toBigDecimal(model.getInsurancePremiumRatio()),
            model.getActive());
    }

    private static RateFormula toRateFormula(String value) {
        return value != null ? RateFormula.valueOf(value) : null;
    }
}
