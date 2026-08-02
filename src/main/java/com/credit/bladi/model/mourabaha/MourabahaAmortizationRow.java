package com.credit.bladi.model.mourabaha;

import com.credit.bladi.model.vo.Money;

public record MourabahaAmortizationRow(
        int installmentNumber,
        Money beginningBalance,
        Money marginHt,
        Money vatOnMargin,
        Money marginTtc,
        Money principal,
        Money payment,
        Money endingBalance
) {

    public boolean equalsValue(MourabahaAmortizationRow other) {
        if (other == null) {
            return false;
        }
        return installmentNumber == other.installmentNumber
            && moneyEquals(beginningBalance, other.beginningBalance)
            && moneyEquals(marginHt, other.marginHt)
            && moneyEquals(vatOnMargin, other.vatOnMargin)
            && moneyEquals(marginTtc, other.marginTtc)
            && moneyEquals(principal, other.principal)
            && moneyEquals(payment, other.payment)
            && moneyEquals(endingBalance, other.endingBalance);
    }

    public int valueHashCode() {
        int result = Integer.hashCode(installmentNumber);
        result = 31 * result + moneyHash(beginningBalance);
        result = 31 * result + moneyHash(marginHt);
        result = 31 * result + moneyHash(vatOnMargin);
        result = 31 * result + moneyHash(marginTtc);
        result = 31 * result + moneyHash(principal);
        result = 31 * result + moneyHash(payment);
        result = 31 * result + moneyHash(endingBalance);
        return result;
    }

    private static boolean moneyEquals(Money a, Money b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static int moneyHash(Money m) {
        return m == null ? 0 : m.valueHashCode();
    }
}
