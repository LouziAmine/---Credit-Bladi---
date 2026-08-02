package com.credit.bladi.exception;

import java.math.BigDecimal;

public class BAMException extends ApiException {

    private final BAMExceptionType type;

    private final String message;

    public enum BAMExceptionType {
        DURATION_LIMIT_EXCEEDED,
        DEPOSIT_INSUFFICIENT,
        DEBT_RATIO_EXCEEDED,
        AGE_LIMIT_EXCEEDED
    }

    private BAMException(BAMExceptionType type, ApiStatus status, String message) {
        super(status);
        this.type = type;
        this.message = message;
    }

    public static BAMException of(BAMExceptionType type) {
        return switch (type) {
            case DURATION_LIMIT_EXCEEDED -> new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "Duration out of range for this CSP profile (BAM regulation).");
            case AGE_LIMIT_EXCEEDED -> new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "Credit maturity exceeds the maximum allowed age at maturity (BAM regulation).");
            case DEPOSIT_INSUFFICIENT, DEBT_RATIO_EXCEEDED -> throw new IllegalArgumentException(
                type + " requires the configured ratio — use of(type, ratio) instead");
        };
    }

    public static BAMException of(BAMExceptionType type, BigDecimal ratio) {
        BigDecimal percentage = asPercentage(ratio);
        return switch (type) {
            case DEPOSIT_INSUFFICIENT -> {
                BigDecimal minDepositPercentage = BigDecimal.valueOf(100).subtract(percentage);
                yield new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                    "Loan amount exceeds " + plain(percentage) + "% of property value. A minimum deposit of "
                        + plain(minDepositPercentage) + "% is required (BAM regulation).");
            }
            case DEBT_RATIO_EXCEEDED -> new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "Monthly payment exceeds " + plain(percentage) + "% of declared monthly income (BAM regulation).");
            case DURATION_LIMIT_EXCEEDED, AGE_LIMIT_EXCEEDED -> throw new IllegalArgumentException(
                type + " does not take a ratio parameter — use of(type) instead");
        };
    }

    private static BigDecimal asPercentage(BigDecimal ratio) {
        return ratio.multiply(BigDecimal.valueOf(100));
    }

    private static String plain(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    public BAMExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }
}