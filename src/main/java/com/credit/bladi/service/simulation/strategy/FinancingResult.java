package com.credit.bladi.service.simulation.strategy;

import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.simulation.AmortizationRow;
import com.credit.bladi.model.simulation.FinancialSnapshot;
import com.credit.bladi.model.simulation.RateBoundsSnapshot;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;

public record FinancingResult(
        Rate applicableRate,
        Rate rateMin,
        Rate rateMax,
        FinancialSnapshot applicableSnapshot,
        RateBoundsSnapshot bounds,
        MourabahaFinancialSnapshot mourabahaSnapshot,
        List<AmortizationRow> amortizationSchedule,
        List<MourabahaAmortizationRow> mourabahaAmortizationSchedule,
        BigDecimal taeg
) {
}
