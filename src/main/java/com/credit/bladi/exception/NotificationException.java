package com.credit.bladi.exception;

public class NotificationException extends ApiException {
    private final NotificationExceptionType type;

    public enum NotificationExceptionType {
        NOTIFICATION_NOT_FOUND
    }

    private NotificationException(NotificationExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static NotificationException of(NotificationExceptionType type) {
        return switch (type) {
            case NOTIFICATION_NOT_FOUND ->
                new NotificationException(type, ApiStatus.NOT_FOUND, "notification.notFound");
        };
    }

    public NotificationExceptionType getType() {
        return type;
    }
}
