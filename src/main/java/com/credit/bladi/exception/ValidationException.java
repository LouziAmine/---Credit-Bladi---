package com.credit.bladi.exception;

public class ValidationException extends ApiException {

    private final ValidationExceptionType type;

    private final String detail;

    public enum ValidationExceptionType {
        BATCH_NOT_ALLOWED, UNKNOWN_KEY, INVALID_PAGE, INVALID_SIZE, INVALID_VALUE
    }

    private ValidationException(ApiStatus status, ValidationExceptionType type, String detail) {
        super(status);
        this.type = type;
        this.detail = detail;
    }

    private ValidationException(ApiStatus status, ValidationExceptionType type) {
        super(status);
        this.type = type;
        this.detail = null;
    }

    public static ValidationException of(ValidationExceptionType type) {
        return of(type, null);
    }

    public static ValidationException of(ValidationExceptionType type, String detail) {
        return switch (type) {
            case BATCH_NOT_ALLOWED, INVALID_PAGE, INVALID_SIZE ->
                new ValidationException(ApiStatus.BAD_REQUEST, type);
            case UNKNOWN_KEY, INVALID_VALUE ->
                new ValidationException(ApiStatus.BAD_REQUEST, type, detail);
        };
    }

    public ValidationExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return switch (type) {
            case BATCH_NOT_ALLOWED -> "BATCH_NOT_ALLOWED: only one JSON object is allowed per request.";
            case UNKNOWN_KEY -> "UNKNOWN_KEY: " + (detail != null ? detail : "unrecognized field.");
            case INVALID_PAGE -> "INVALID_PAGE: page must be greater or equal to 1.";
            case INVALID_SIZE -> "INVALID_SIZE: size must be between 1 and 50.";
            case INVALID_VALUE -> "INVALID_VALUE: " + (detail != null ? detail : "unsupported value.");
        };
    }
}