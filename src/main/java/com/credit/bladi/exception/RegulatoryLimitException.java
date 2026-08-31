package com.credit.bladi.exception;

public class RegulatoryLimitException extends ApiException {
    private final RegulatoryLimitExceptionType type;

    public enum RegulatoryLimitExceptionType {
        RATE_ABOVE_LEGAL_MAXIMUM
    }

    private RegulatoryLimitException(RegulatoryLimitExceptionType type, ApiStatus status, String messageKey,
            Object... args) {
        super(status, messageKey, args);
        this.type = type;
    }

    public static RegulatoryLimitException rateAboveLegalMaximum(String rate, String maximum) {
        return new RegulatoryLimitException(RegulatoryLimitExceptionType.RATE_ABOVE_LEGAL_MAXIMUM,
            ApiStatus.UNPROCESSABLE_CONTENT, "regulatoryLimit.rateAboveLegalMaximum", rate, maximum);
    }

    public RegulatoryLimitExceptionType getType() {
        return type;
    }
}
