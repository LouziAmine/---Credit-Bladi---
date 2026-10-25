package com.credit.bladi.model.workflow;

import com.credit.bladi.model.vo.Money;

public record DemandAmortizationResult(
        DemandModel demand,
        Money resolvedAmount,
        Integer resolvedDuration,
        DemandAmortizationSnapshot snapshot
) {
}
