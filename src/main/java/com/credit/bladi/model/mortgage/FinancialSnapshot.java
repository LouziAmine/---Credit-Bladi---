package com.credit.bladi.model.mortgage;

import com.credit.bladi.model.vo.Money;

public record FinancialSnapshot(
        Money monthlyPayment,
        Money adiMonthlyPremium,
        Money allInMonthlyPayment,
        Money totalInterest,
        Money totalAdiCost,
        Money totalCost,
        Money registrationFees,
        Money landConservationFees,
        Money notaryFees,
        Money processingFees,
        Money totalFees
) {
}
