package com.credit.bladi.service.workflow.product;

import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationResult;
import com.credit.bladi.model.workflow.DemandModel;

public interface DemandAmortizationProvider extends DemandTypeStrategy {
    DemandAmortizationResult getAmortizationSchedule(DemandModel demand, Money amountOverride,
            Rate rateOverride, Integer durationOverride);
}
