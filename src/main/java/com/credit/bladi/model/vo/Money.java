package com.credit.bladi.model.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount) {
    public Money {
        Objects.requireNonNull(amount, "amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative: " + amount);
        }
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "other cannot be null");
        return new Money(amount.add(other.amount).setScale(2, RoundingMode.HALF_UP));
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "other cannot be null");
        return new Money(amount.subtract(other.amount).setScale(2, RoundingMode.HALF_UP));
    }

    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor cannot be null");
        return new Money(amount.multiply(factor).setScale(2, RoundingMode.HALF_UP));
    }

    public boolean equalsValue(Money other) {
        return other != null && amount.compareTo(other.amount) == 0;
    }

    public int valueHashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Money other && equalsValue(other);
    }

    @Override
    public int hashCode() {
        return valueHashCode();
    }

    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        return amount.compareTo(other.amount) < 0;
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " MAD";
    }
}
