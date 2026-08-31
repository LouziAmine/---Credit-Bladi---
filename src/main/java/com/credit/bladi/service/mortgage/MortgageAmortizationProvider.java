package com.credit.bladi.service.mortgage;

import com.credit.bladi.model.enums.DemandType;
import com.credit.bladi.model.mortgage.MortgageAmortizationSnapshot;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.service.workflow.product.DemandAmortizationProvider;
import org.springframework.stereotype.Component;

@Component
public class MortgageAmortizationProvider implements DemandAmortizationProvider {
    private final IMortgageCreditService mortgageCreditService;

    public MortgageAmortizationProvider(IMortgageCreditService mortgageCreditService) {
        this.mortgageCreditService = mortgageCreditService;
    }

    @Override
    public DemandType supportedType() {
        return DemandType.MORTGAGE;
    }

    @Override
    public DemandAmortizationResult getAmortizationSchedule(DemandModel demand, Money amountOverride,
            Rate rateOverride, Integer durationOverride) {
        Money resolvedAmount = amountOverride != null ? amountOverride : demand.amount();
        Integer resolvedDuration = durationOverride != null ? durationOverride : demand.duration();
        MortgageAmortizationSnapshot snapshot = mortgageCreditService.getAmortizationSchedule(demand.id(),
            amountOverride, rateOverride, durationOverride);
        return new DemandAmortizationResult(demand, resolvedAmount, resolvedDuration, snapshot);
    }
}
