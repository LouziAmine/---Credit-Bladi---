package com.credit.bladi.exception;

public class ActivitySectorException extends ApiException {
    private final ActivitySectorExceptionType type;

    public enum ActivitySectorExceptionType {
        DUPLICATE_NAME
    }

    private ActivitySectorException(ActivitySectorExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static ActivitySectorException of(ActivitySectorExceptionType type) {
        return switch (type) {
            case DUPLICATE_NAME ->
                new ActivitySectorException(type, ApiStatus.CONFLICT, "activitySector.duplicateName");
        };
    }

    public ActivitySectorExceptionType getType() {
        return type;
    }
}
