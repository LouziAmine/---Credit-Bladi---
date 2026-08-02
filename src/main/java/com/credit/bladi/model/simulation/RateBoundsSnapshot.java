package com.credit.bladi.model.simulation;

public record RateBoundsSnapshot(FinancialSnapshot min, FinancialSnapshot max) {

    public boolean equalsValue(RateBoundsSnapshot other) {
        if (other == null) {
            return false;
        }
        return snapshotEquals(min, other.min) && snapshotEquals(max, other.max);
    }

    public int valueHashCode() {
        return 31 * snapshotHash(min) + snapshotHash(max);
    }

    private static boolean snapshotEquals(FinancialSnapshot a, FinancialSnapshot b) {
        return a == null ? b == null : a.equalsValue(b);
    }

    private static int snapshotHash(FinancialSnapshot s) {
        return s == null ? 0 : s.valueHashCode();
    }
}