package com.credit.bladi.model.vo;

import java.math.BigDecimal;

public final class ValueObjectMappers {
    private ValueObjectMappers() {
    }

    public static Rate toRate(BigDecimal value) {
        return value != null ? Rate.of(value) : null;
    }

    public static Ratio toRatio(BigDecimal value) {
        return value != null ? Ratio.of(value) : null;
    }

    public static Money toMoney(BigDecimal value) {
        return value != null ? Money.of(value) : null;
    }

    public static Rib toRib(String value) {
        return value != null ? Rib.of(value) : null;
    }

    public static String toRibValue(Rib rib) {
        return rib != null ? rib.value() : null;
    }

    public static BigDecimal toBigDecimal(Rate rate) {
        return rate != null ? rate.value() : null;
    }

    public static BigDecimal toBigDecimal(Ratio ratio) {
        return ratio != null ? ratio.value() : null;
    }

    public static BigDecimal toBigDecimal(Money money) {
        return money != null ? money.amount() : null;
    }

    public static boolean moneyEquals(Money first, Money second) {
        return first == null ? second == null : first.equalsValue(second);
    }

    public static int moneyHashCode(Money money) {
        return money == null ? 0 : money.valueHashCode();
    }
}
