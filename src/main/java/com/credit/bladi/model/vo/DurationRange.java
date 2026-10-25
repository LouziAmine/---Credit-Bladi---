package com.credit.bladi.model.vo;

import static com.credit.bladi.exception.CreditRateException.CreditRateExceptionType.INVALID_DURATION_RANGE;

import com.credit.bladi.exception.CreditRateException;

public record DurationRange(int minMonths, int maxMonths) {
    public DurationRange {
        if (minMonths < 0) {
            throw new IllegalArgumentException("minMonths cannot be negative: " + minMonths);
        }
        if (maxMonths < minMonths) {
            throw new IllegalArgumentException(
                "maxMonths (" + maxMonths + ") cannot be less than minMonths (" + minMonths + ")");
        }
    }

    public static DurationRange of(int minMonths, int maxMonths) {
        return new DurationRange(minMonths, maxMonths);
    }

    public static DurationRange ofNullable(Integer minMonths, Integer maxMonths) {
        if (minMonths == null && maxMonths == null) {
            return null;
        }
        if (minMonths == null || maxMonths == null) {
            throw CreditRateException.of(INVALID_DURATION_RANGE);
        }
        return of(minMonths, maxMonths);
    }

    public boolean contains(int months) {
        return months >= minMonths && months <= maxMonths;
    }

    @Override
    public String toString() {
        return "[" + minMonths + ", " + maxMonths + "] months";
    }
}
