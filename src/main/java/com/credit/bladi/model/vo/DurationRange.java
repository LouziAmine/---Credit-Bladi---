package com.credit.bladi.model.vo;

import static com.credit.bladi.exception.BAMException.BAMExceptionType.DURATION_LIMIT_EXCEEDED;

import com.credit.bladi.exception.BAMException;

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

    public boolean contains(int months) {
        return months >= minMonths && months <= maxMonths;
    }

    public void assertContains(int months) {
        if (!contains(months)) {
            throw BAMException.of(DURATION_LIMIT_EXCEEDED);
        }
    }

    @Override
    public String toString() {
        return "[" + minMonths + ", " + maxMonths + "] months";
    }
}