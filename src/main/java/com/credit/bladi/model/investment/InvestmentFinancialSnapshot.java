package com.credit.bladi.model.investment;

import com.credit.bladi.model.vo.AmortizationRow;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.DemandAmortizationSnapshot;
import java.math.BigDecimal;
import java.util.List;

public record InvestmentFinancialSnapshot(
        Rate applicableRate,
        Money monthlyPayment,
        Money annualDebtService,
        Money totalInterest,
        Money totalCost,
        Money processingFees,
        BigDecimal taeg,
        List<AmortizationRow> amortizationSchedule,
        Money insurancePremium,
        Money allInMonthlyPayment,
        Money totalInsuranceCost
) implements DemandAmortizationSnapshot {
}
