package com.credit.bladi.exception;

public class BamParametersException extends ApiException {
    private final BamParametersExceptionType type;

    public enum BamParametersExceptionType {
        NOT_FOUND,
        INVALID_PROCESSING_FEES_RANGE
    }

    private BamParametersException(BamParametersExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static BamParametersException of(BamParametersExceptionType type) {
        return switch (type) {
            case NOT_FOUND -> new BamParametersException(type, ApiStatus.NOT_FOUND, "bamParameters.notFound");
            case INVALID_PROCESSING_FEES_RANGE -> new BamParametersException(type, ApiStatus.BAD_REQUEST,
                "bamParameters.invalidProcessingFeesRange");
        };
    }

    public BamParametersExceptionType getType() {
        return type;
    }
}
