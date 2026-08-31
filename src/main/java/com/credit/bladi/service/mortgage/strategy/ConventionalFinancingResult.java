package com.credit.bladi.service.mortgage.strategy;

import com.credit.bladi.model.mortgage.FinancialSnapshot;
import com.credit.bladi.model.mortgage.RateBoundsSnapshot;
import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;

public record ConventionalFinancingResult(
        Rate applicableRate,
        Rate rateMin,
        Rate rateMax,
        FinancialSnapshot applicableSnapshot,
        RateBoundsSnapshot bounds,
        List<AmortizationRow> amortizationSchedule,
        BigDecimal taeg
) implements FinancingResult {
}
