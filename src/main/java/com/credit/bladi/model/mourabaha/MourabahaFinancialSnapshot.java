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
        Money totalFees
) {

    public boolean equalsValue(MourabahaFinancialSnapshot other) {
        if (other == null) {
            return false;
        }
        return moneyEquals(monthlyPaymentTtc, other.monthlyPaymentTtc)
            && moneyEquals(marginHt, other.marginHt)
            && moneyEquals(vatOnMargin, other.vatOnMargin)
            && moneyEquals(marginTtc, other.marginTtc)
            && moneyEquals(totalCostTtc, other.totalCostTtc)
            && moneyEquals(registrationFees, other.registrationFees)
            && moneyEquals(landConservationFees, other.landConservationFees)
            && moneyEquals(notaryFees, other.notaryFees)
            && moneyEquals(processingFees, other.processingFees)
            && moneyEquals(totalFees, other.totalFees);
    }

    public int valueHashCode() {
        int result = moneyHash(monthlyPaymentTtc);
        result = 31 * result + moneyHash(marginHt);
        result = 31 * result + moneyHash(vatOnMargin);
        result = 31 * result + moneyHash(marginTtc);
        result = 31 * result + moneyHash(totalCostTtc);
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
