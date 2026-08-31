package com.credit.bladi.model.mourabaha;

import com.credit.bladi.model.vo.Money;

public record MourabahaFinancialSnapshot(
        Money monthlyPaymentTtc,
        Money marginHt,
        Money vatOnMargin,
        Money marginTtc,
        Money totalCostTtc,
        Money registrationFees,
        Money landConservationFees,
        Money notaryFees,
        Money processingFees,
        Money totalFees,
        Money takafulMonthlyContribution,
        Money allInMonthlyPaymentTtc,
        Money totalTakafulCost
) {
}
