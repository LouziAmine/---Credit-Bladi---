package com.credit.bladi.exception;

public class MourabahaParametersException extends ApiException {
    private final MourabahaParametersExceptionType type;

    public enum MourabahaParametersExceptionType {
        NOT_FOUND
    }

    private MourabahaParametersException(MourabahaParametersExceptionType type, ApiStatus status,
            String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static MourabahaParametersException of(MourabahaParametersExceptionType type) {
        return switch (type) {
            case NOT_FOUND ->
                new MourabahaParametersException(type, ApiStatus.NOT_FOUND, "mourabahaParameters.notFound");
        };
    }

    public MourabahaParametersExceptionType getType() {
        return type;
    }
}
