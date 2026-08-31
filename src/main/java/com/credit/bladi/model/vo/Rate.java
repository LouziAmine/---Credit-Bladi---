package com.credit.bladi.model.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Rate(BigDecimal value) {
    private static final BigDecimal MAX_RATE = BigDecimal.valueOf(100);

    public Rate {
        Objects.requireNonNull(value, "rate value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rate must be strictly positive: " + value);
        }
        if (value.compareTo(MAX_RATE) > 0) {
            throw new IllegalArgumentException("Rate exceeds sane maximum of " + MAX_RATE + "%: " + value);
        }
    }

    public static Rate of(BigDecimal value) {
        return new Rate(value);
    }

    public Rate average(Rate other) {
        Objects.requireNonNull(other, "other cannot be null");
        return new Rate(
            value.add(other.value).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)
        );
    }

    public boolean isGreaterThan(Rate other) {
        return value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(Rate other) {
        return value.compareTo(other.value) < 0;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Rate other && value.compareTo(other.value) == 0;
    }

    @Override
    public int hashCode() {
        return value.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return value.toPlainString() + "%";
    }
}
