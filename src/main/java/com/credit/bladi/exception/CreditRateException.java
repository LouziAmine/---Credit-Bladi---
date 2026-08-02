package com.credit.bladi.exception;

public class CreditRateException extends ApiException {

    private final CreditRateExceptionType type;

    public enum CreditRateExceptionType {
        NOT_FOUND,
        INVALID_DURATION_RANGE
    }

    private CreditRateException(CreditRateExceptionType type, ApiStatus status) {
        super(status);
        this.type = type;
    }

    public static CreditRateException of(CreditRateExceptionType type) {
        return switch (type) {
            case NOT_FOUND -> new CreditRateException(type, ApiStatus.NOT_FOUND);
            case INVALID_DURATION_RANGE -> new CreditRateException(type, ApiStatus.BAD_REQUEST);
        };
    }

    public CreditRateExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return switch (type) {
            case NOT_FOUND ->
                "Credit rate record not found for the given ID.";
            case INVALID_DURATION_RANGE ->
                "minDurationMonths and maxDurationMonths must be provided together.";
        };
    }
}
