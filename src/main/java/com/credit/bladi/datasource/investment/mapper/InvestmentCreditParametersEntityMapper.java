package com.credit.bladi.datasource.investment.mapper;

import static com.credit.bladi.model.vo.ValueObjectMappers.toBigDecimal;

import com.credit.bladi.datasource.investment.entity.InvestmentCreditParametersEntity;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.RateFormula;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;

public final class InvestmentCreditParametersEntityMapper {
    private InvestmentCreditParametersEntityMapper() {
    }

    public static InvestmentCreditParametersModel mapFromEntity(InvestmentCreditParametersEntity entity) {
        return InvestmentCreditParametersModel.getBuilder()
            .id(entity.getId())
            .publicId(entity.getPublicId())
            .enterpriseType(EnterpriseType.valueOf(entity.getEnterpriseType()))
            .rateMin(Rate.of(entity.getRateMin()))
            .rateMax(Rate.of(entity.getRateMax()))
            .rateFormula(entity.getRateFormula() != null ? RateFormula.valueOf(entity.getRateFormula()) : null)
            .minCompanyAgeYears(entity.getMinCompanyAgeYears())
            .maxFinancingToRevenueRatio(Ratio.of(entity.getMaxFinancingToRevenueRatio()))
            .minGuaranteeCoverageRatio(Ratio.of(entity.getMinGuaranteeCoverageRatio()))
            .maxDebtServiceRatio(Ratio.of(entity.getMaxDebtServiceRatio()))
            .processingFeesRatio(Ratio.of(entity.getProcessingFeesRatio()))
            .insurancePremiumRatio(Ratio.of(entity.getInsurancePremiumRatio()))
            .active(entity.getActive())
            .build();
    }

    public static InvestmentCreditParametersEntity mapToEntity(InvestmentCreditParametersModel model) {
        InvestmentCreditParametersEntity entity = new InvestmentCreditParametersEntity();
        entity.setId(model.getId());
        entity.setEnterpriseType(model.getEnterpriseType() != null ? model.getEnterpriseType().name() : null);
        entity.setRateMin(toBigDecimal(model.getRateMin()));
        entity.setRateMax(toBigDecimal(model.getRateMax()));
        entity.setRateFormula(model.getRateFormula() != null ? model.getRateFormula().name() : null);
        entity.setMinCompanyAgeYears(model.getMinCompanyAgeYears());
        entity.setMaxFinancingToRevenueRatio(toBigDecimal(model.getMaxFinancingToRevenueRatio()));
        entity.setMinGuaranteeCoverageRatio(toBigDecimal(model.getMinGuaranteeCoverageRatio()));
        entity.setMaxDebtServiceRatio(toBigDecimal(model.getMaxDebtServiceRatio()));
        entity.setProcessingFeesRatio(toBigDecimal(model.getProcessingFeesRatio()));
        entity.setInsurancePremiumRatio(toBigDecimal(model.getInsurancePremiumRatio()));
        entity.setActive(model.getActive());
        return entity;
    }
}
