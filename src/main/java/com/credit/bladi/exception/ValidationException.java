package com.credit.bladi.exception;

public class ValidationException extends ApiException {
    private final ValidationExceptionType type;

    public enum ValidationExceptionType {
        BATCH_NOT_ALLOWED, UNKNOWN_KEY, INVALID_VALUE
    }

    private ValidationException(ValidationExceptionType type, ApiStatus status, String messageKey, Object... args) {
        super(status, messageKey, args);
        this.type = type;
    }

    public static ValidationException of(ValidationExceptionType type) {
        return of(type, null);
    }

    public static ValidationException of(ValidationExceptionType type, String detail) {
        return switch (type) {
            case BATCH_NOT_ALLOWED ->
                new ValidationException(type, ApiStatus.BAD_REQUEST, "validation.batchNotAllowed");
            case UNKNOWN_KEY -> new ValidationException(type, ApiStatus.BAD_REQUEST, "validation.unknownKey", detail);
            case INVALID_VALUE -> new ValidationException(type, ApiStatus.BAD_REQUEST, "validation.invalidValue",
                detail);
        };
    }

    public static ValidationException requiredTogether(String first, String second) {
        return new ValidationException(ValidationExceptionType.INVALID_VALUE, ApiStatus.BAD_REQUEST,
            "validation.parametersRequiredTogether", first, second);
    }

    public ValidationExceptionType getType() {
        return type;
    }
}
