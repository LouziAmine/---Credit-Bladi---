package com.credit.bladi.model.simulation;

import com.credit.bladi.model.vo.Money;

public record AmortizationRow(
        int installmentNumber,
        Money beginningBalance,
        Money interest,
        Money principal,
        Money payment,
        Money endingBalance
) {

    public boolean equalsValue(AmortizationRow other) {
        if (other == null) {
            return false;
        }
        return installmentNumber == other.installmentNumber
            && moneyEquals(beginningBalance, other.beginningBalance)
            && moneyEquals(interest, other.interest)
            && moneyEquals(principal, other.principal)
            && moneyEquals(payment, other.payment)
            && moneyEquals(endingBalance, other.endingBalance);
    }

    public int valueHashCode() {
        int result = Integer.hashCode(installmentNumber);
        result = 31 * result + moneyHash(beginningBalance);
        result = 31 * result + moneyHash(interest);
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
