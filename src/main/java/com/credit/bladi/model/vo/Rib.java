package com.credit.bladi.model.vo;

import java.math.BigInteger;
import java.util.Objects;
import java.util.regex.Pattern;

public record Rib(String value) {
    private static final Pattern DIGITS_24 = Pattern.compile("\\d{24}");

    private static final BigInteger MODULUS = BigInteger.valueOf(97);

    private static final int VISIBLE_TRAILING_DIGITS = 4;

    public Rib {
        Objects.requireNonNull(value, "RIB value cannot be null");
        String normalized = value.replaceAll("[ -]", "");
        if (!DIGITS_24.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                "RIB must be exactly 24 digits (3 bank + 3 city + 16 account + 2 key)");
        }
        if (!new BigInteger(normalized).mod(MODULUS).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("RIB checksum key is invalid");
        }
        value = normalized;
    }

    public static Rib of(String value) {
        return new Rib(value);
    }

    public String masked() {
        return "*".repeat(value.length() - VISIBLE_TRAILING_DIGITS)
            + value.substring(value.length() - VISIBLE_TRAILING_DIGITS);
    }

    @Override
    public String toString() {
        return value;
    }
}
