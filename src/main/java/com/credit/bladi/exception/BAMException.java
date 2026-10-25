package com.credit.bladi.exception;

import java.math.BigDecimal;

public class BAMException extends ApiException {
    private final BAMExceptionType type;

    public enum BAMExceptionType {
        DURATION_LIMIT_EXCEEDED,
        DEPOSIT_INSUFFICIENT,
        DEBT_RATIO_EXCEEDED,
        AGE_LIMIT_EXCEEDED
    }

    private BAMException(BAMExceptionType type, ApiStatus status, String messageKey, Object... args) {
        super(status, messageKey, args);
        this.type = type;
    }

    public static BAMException of(BAMExceptionType type) {
        return switch (type) {
            case DURATION_LIMIT_EXCEEDED ->
                new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT, "bam.durationLimitExceeded");
            case AGE_LIMIT_EXCEEDED ->
                new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT, "bam.ageLimitExceeded");
            case DEPOSIT_INSUFFICIENT, DEBT_RATIO_EXCEEDED -> throw new IllegalArgumentException(
                type + " requires the configured ratio: use of(type, ratio) instead");
        };
    }

    public static BAMException of(BAMExceptionType type, BigDecimal ratio) {
        BigDecimal percentage = asPercentage(ratio);
        return switch (type) {
            case DEPOSIT_INSUFFICIENT -> {
                BigDecimal minDepositPercentage = BigDecimal.valueOf(100).subtract(percentage);
                yield new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                    "bam.depositInsufficient", plain(percentage), plain(minDepositPercentage));
            }
            case DEBT_RATIO_EXCEEDED -> new BAMException(type, ApiStatus.UNPROCESSABLE_CONTENT,
                "bam.debtRatioExceeded", plain(percentage));
            case DURATION_LIMIT_EXCEEDED, AGE_LIMIT_EXCEEDED -> throw new IllegalArgumentException(
                type + " does not take a ratio parameter: use of(type) instead");
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
}
