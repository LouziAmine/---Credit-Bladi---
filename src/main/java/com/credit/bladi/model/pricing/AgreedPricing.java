package com.credit.bladi.model.pricing;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.investment.InvestmentCreditParametersModel;
import com.credit.bladi.model.mourabaha.MourabahaParametersModel;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.vo.Ratio;
import java.time.Instant;

public record AgreedPricing(
        Long demandId,
        MortgageFees mortgageFees,
        MourabahaTerms mourabahaTerms,
        InvestmentFees investmentFees,
        Instant capturedAt,
        String capturedBy
) {
    public record MortgageFees(Ratio processingFeesRatio, Money minProcessingFees, Money maxProcessingFees,
            Ratio registrationFeeRatio, Ratio landConservationRatio, Ratio notaryFeeRatio, Ratio adiMonthlyRate) {
        public static MortgageFees of(BamParametersModel parameters) {
            return new MortgageFees(parameters.getProcessingFeesRatio(), parameters.getMinProcessingFees(),
                parameters.getMaxProcessingFees(), parameters.getRegistrationFeeRatio(),
                parameters.getLandConservationRatio(), parameters.getNotaryFeeRatio(), parameters.getAdiMonthlyRate());
        }
    }

    public record MourabahaTerms(Ratio vatRate, Rate takafulMonthlyRate) {
        public static MourabahaTerms of(MourabahaParametersModel parameters) {
            return new MourabahaTerms(parameters.getVatRate(), parameters.getTakafulMonthlyRate());
        }
    }

    public record InvestmentFees(Ratio processingFeesRatio, Ratio insurancePremiumRatio) {
        public static InvestmentFees of(InvestmentCreditParametersModel parameters) {
            return new InvestmentFees(parameters.getProcessingFeesRatio(), parameters.getInsurancePremiumRatio());
        }
    }

    public BamParametersModel applyTo(BamParametersModel active) {
        if (mortgageFees == null) {
            return active;
        }
        return BamParametersModel.getBuilder().id(active.getId()).publicId(active.getPublicId())
            .processingFeesRatio(mortgageFees.processingFeesRatio())
            .minProcessingFees(mortgageFees.minProcessingFees())
            .maxProcessingFees(mortgageFees.maxProcessingFees())
            .registrationFeeRatio(mortgageFees.registrationFeeRatio())
            .landConservationRatio(mortgageFees.landConservationRatio())
            .notaryFeeRatio(mortgageFees.notaryFeeRatio())
            .ltvRatio(active.getLtvRatio())
            .maxDebtRatio(active.getMaxDebtRatio())
            .maxAgeAtMaturity(active.getMaxAgeAtMaturity())
            .adiMonthlyRate(mortgageFees.adiMonthlyRate())
            .active(active.getActive())
            .build();
    }

    public MourabahaParametersModel applyTo(MourabahaParametersModel active) {
        if (mourabahaTerms == null) {
            return active;
        }
        return MourabahaParametersModel.getBuilder().id(active.getId()).publicId(active.getPublicId())
            .profitRate(active.getProfitRate())
            .vatRate(mourabahaTerms.vatRate())
            .takafulMonthlyRate(mourabahaTerms.takafulMonthlyRate())
            .active(active.getActive())
            .build();
    }

    public InvestmentCreditParametersModel applyTo(InvestmentCreditParametersModel active) {
        if (investmentFees == null) {
            return active;
        }
        return InvestmentCreditParametersModel.getBuilder().id(active.getId()).publicId(active.getPublicId())
            .enterpriseType(active.getEnterpriseType())
            .rateMin(active.getRateMin())
            .rateMax(active.getRateMax())
            .rateFormula(active.getRateFormula())
            .minCompanyAgeYears(active.getMinCompanyAgeYears())
            .maxFinancingToRevenueRatio(active.getMaxFinancingToRevenueRatio())
            .minGuaranteeCoverageRatio(active.getMinGuaranteeCoverageRatio())
            .maxDebtServiceRatio(active.getMaxDebtServiceRatio())
            .processingFeesRatio(investmentFees.processingFeesRatio())
            .insurancePremiumRatio(investmentFees.insurancePremiumRatio())
            .active(active.getActive())
            .build();
    }
}
