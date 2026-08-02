package com.credit.bladi.model.vo;

import java.math.BigDecimal;
import java.util.Objects;

public record Ratio(BigDecimal value) {

    public Ratio {
        Objects.requireNonNull(value, "ratio value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ratio must be strictly positive: " + value);
        }
        if (value.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Ratio cannot exceed 1 (100%): " + value);
        }
    }

    public static Ratio of(BigDecimal value) {
        return new Ratio(value);
    }

    public boolean isGreaterThan(Ratio other) {
        return value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(Ratio other) {
        return value.compareTo(other.value) < 0;
    }

    public boolean equalsValue(Ratio other) {
        return other != null && value.compareTo(other.value) == 0;
    }

    public int valueHashCode() {
        return value.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}