package com.credit.bladi.service.mortgage.strategy;

import com.credit.bladi.model.bam.BamParametersModel;
import com.credit.bladi.model.enums.ProductType;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.mortgage.request.MortgageCreditRequest;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.MortgageDemandDetail;

public interface FinancingCalculationStrategy {
    ProductType getProductType();

    FinancingResult simulate(MortgageCreditRequest request, BamParametersModel bamParameters, Money totalMonthlyIncome);

    void assertEligible(MortgageDemandDetail detail, Money amount, Rate rate, int duration,
            BamParametersModel bamParameters);

    MortgageAmortizationSnapshot computeAmortizationSchedule(DemandModel demand, Money amount, int duration,
            Rate rateOverride, Money propertyValue, BamParametersModel bamParameters, boolean coBorrower);
}
