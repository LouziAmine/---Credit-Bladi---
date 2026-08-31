package com.credit.bladi.service.investment;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.investment.InvestmentFinancialSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.workflow.product.DemandAmortizationProvider;
import org.springframework.stereotype.Component;

@Component
public class InvestmentAmortizationProvider implements DemandAmortizationProvider {
    private final IInvestmentCreditService investmentCreditService;

    public InvestmentAmortizationProvider(IInvestmentCreditService investmentCreditService) {
        this.investmentCreditService = investmentCreditService;
    }

    @Override
    public DemandType supportedType() {
        return DemandType.ENTERPRISE;
    }

    @Override
    public DemandAmortizationResult getAmortizationSchedule(DemandModel demand, Money amountOverride,
            Rate rateOverride, Integer durationOverride) {
        Money resolvedAmount = amountOverride != null ? amountOverride : demand.amount();
        Integer resolvedDuration = durationOverride != null ? durationOverride : demand.duration();
        InvestmentFinancialSnapshot snapshot = investmentCreditService.getAmortizationSchedule(demand.id(),
            amountOverride, rateOverride, durationOverride);
        return new DemandAmortizationResult(demand, resolvedAmount, resolvedDuration, snapshot);
    }
}
