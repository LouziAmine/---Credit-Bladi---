package com.credit.bladi.exception;

public class MourabahaParametersException extends ApiException {

    private final MourabahaParametersExceptionType type;

    public enum MourabahaParametersExceptionType {
        NOT_FOUND
    }

    private MourabahaParametersException(MourabahaParametersExceptionType type, ApiStatus status) {
        super(status);
        this.type = type;
    }

    public static MourabahaParametersException of(MourabahaParametersExceptionType type) {
        return switch (type) {
            case NOT_FOUND -> new MourabahaParametersException(type, ApiStatus.NOT_FOUND);
        };
    }

    public MourabahaParametersExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return switch (type) {
            case NOT_FOUND -> "Mourabaha parameters record not found for the given ID.";
        };
    }
}
