package com.credit.bladi.exception;

public class BamParametersException extends ApiException {

    private final BamParametersExceptionType type;

    public enum BamParametersExceptionType {
        NOT_FOUND,
        INVALID_PROCESSING_FEES_RANGE
    }

    private BamParametersException(BamParametersExceptionType type, ApiStatus status) {
        super(status);
        this.type = type;
    }

    public static BamParametersException of(BamParametersExceptionType type) {
        return switch (type) {
            case NOT_FOUND -> new BamParametersException(type, ApiStatus.NOT_FOUND);
            case INVALID_PROCESSING_FEES_RANGE -> new BamParametersException(type, ApiStatus.BAD_REQUEST);
        };
    }

    public BamParametersExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return switch (type) {
            case NOT_FOUND ->
                "BAM parameters record not found for the given ID.";
            case INVALID_PROCESSING_FEES_RANGE ->
                "minProcessingFees cannot be greater than maxProcessingFees.";
        };
    }
}