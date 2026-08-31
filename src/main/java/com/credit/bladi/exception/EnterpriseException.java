package com.credit.bladi.exception;

public class EnterpriseException extends ApiException {
    private final EnterpriseExceptionType type;

    public enum EnterpriseExceptionType {
        ENTERPRISE_NOT_FOUND
    }

    private EnterpriseException(EnterpriseExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static EnterpriseException of(EnterpriseExceptionType type) {
        return switch (type) {
            case ENTERPRISE_NOT_FOUND -> new EnterpriseException(type, ApiStatus.NOT_FOUND, "enterprise.notFound");
        };
    }

    public EnterpriseExceptionType getType() {
        return type;
    }
}
