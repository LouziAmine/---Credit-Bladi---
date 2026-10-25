package com.credit.bladi.exception;

public class DocumentRequestException extends ApiException {
    private final DocumentRequestExceptionType type;

    public enum DocumentRequestExceptionType {
        REQUEST_NOT_FOUND,
        REQUEST_NOT_FOR_DEMAND,
        REQUEST_NOT_FULFILLED
    }

    private DocumentRequestException(DocumentRequestExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static DocumentRequestException of(DocumentRequestExceptionType type) {
        return switch (type) {
            case REQUEST_NOT_FOUND ->
                new DocumentRequestException(type, ApiStatus.NOT_FOUND, "documentRequest.notFound");
            case REQUEST_NOT_FOR_DEMAND ->
                new DocumentRequestException(type, ApiStatus.NOT_FOUND, "documentRequest.notForDemand");
            case REQUEST_NOT_FULFILLED ->
                new DocumentRequestException(type, ApiStatus.CONFLICT, "documentRequest.notFulfilled");
        };
    }

    public DocumentRequestExceptionType getType() {
        return type;
    }
}
