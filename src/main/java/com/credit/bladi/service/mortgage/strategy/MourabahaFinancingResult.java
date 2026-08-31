package com.credit.bladi.service.mortgage.strategy;

import com.credit.bladi.model.mourabaha.MourabahaAmortizationRow;
import com.credit.bladi.model.mourabaha.MourabahaFinancialSnapshot;
import com.credit.bladi.model.vo.Rate;
import java.math.BigDecimal;
import java.util.List;

public record MourabahaFinancingResult(
        Rate applicableRate,
        MourabahaFinancialSnapshot mourabahaSnapshot,
        List<MourabahaAmortizationRow> mourabahaAmortizationSchedule,
        BigDecimal taeg
) implements FinancingResult {
}
