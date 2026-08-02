package com.credit.bladi.model.simulation;

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

    public boolean equalsValue(FinancialSnapshot other) {
        if (other == null) {
            return false;
        }
        return moneyEquals(monthlyPayment, other.monthlyPayment)
            && moneyEquals(adiMonthlyPremium, other.adiMonthlyPremium)
            && moneyEquals(allInMonthlyPayment, other.allInMonthlyPayment)
            && moneyEquals(totalInterest, other.totalInterest)
            && moneyEquals(totalAdiCost, other.totalAdiCost)
            && moneyEquals(totalCost, other.totalCost)
            && moneyEquals(registrationFees, other.registrationFees)
            && moneyEquals(landConservationFees, other.landConservationFees)
            && moneyEquals(notaryFees, other.notaryFees)
            && moneyEquals(processingFees, other.processingFees)
            && moneyEquals(totalFees, other.totalFees);
    }

    public int valueHashCode() {
        int result = moneyHash(monthlyPayment);
        result = 31 * result + moneyHash(adiMonthlyPremium);
        result = 31 * result + moneyHash(allInMonthlyPayment);
        result = 31 * result + moneyHash(totalInterest);
        result = 31 * result + moneyHash(totalAdiCost);
        result = 31 * result + moneyHash(totalCost);
        result = 31 * result + moneyHash(registrationFees);
        result = 31 * result + moneyHash(landConservationFees);
        result = 31 * result + moneyHash(notaryFees);
        result = 31 * result + moneyHash(processingFees);
        result = 31 * result + moneyHash(totalFees);
        return result;
    }

    private static boolean moneyEquals(Money a, Money b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static int moneyHash(Money m) {
        return m == null ? 0 : m.valueHashCode();
    }
}