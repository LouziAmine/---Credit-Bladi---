package com.credit.bladi.exception;

public class CreditRateException extends ApiException {
    private final CreditRateExceptionType type;

    public enum CreditRateExceptionType {
        NOT_FOUND,
        INVALID_DURATION_RANGE
    }

    private CreditRateException(CreditRateExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static CreditRateException of(CreditRateExceptionType type) {
        return switch (type) {
            case NOT_FOUND -> new CreditRateException(type, ApiStatus.NOT_FOUND, "creditRate.notFound");
            case INVALID_DURATION_RANGE ->
                new CreditRateException(type, ApiStatus.BAD_REQUEST, "creditRate.invalidDurationRange");
        };
    }

    public CreditRateExceptionType getType() {
        return type;
    }
}
