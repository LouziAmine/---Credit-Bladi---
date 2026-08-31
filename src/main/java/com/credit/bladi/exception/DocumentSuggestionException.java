package com.credit.bladi.exception;

public class DocumentSuggestionException extends ApiException {
    private final DocumentSuggestionExceptionType type;

    public enum DocumentSuggestionExceptionType {
        DUPLICATE_LABEL,
        NOT_FOUND,
        SCOPE_CONFLICT
    }

    private DocumentSuggestionException(DocumentSuggestionExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static DocumentSuggestionException of(DocumentSuggestionExceptionType type) {
        return switch (type) {
            case DUPLICATE_LABEL -> new DocumentSuggestionException(type, ApiStatus.CONFLICT,
                "documentSuggestion.duplicateLabel");
            case NOT_FOUND -> new DocumentSuggestionException(type, ApiStatus.NOT_FOUND,
                "documentSuggestion.notFound");
            case SCOPE_CONFLICT -> new DocumentSuggestionException(type, ApiStatus.BAD_REQUEST,
                "documentSuggestion.scopeConflict");
        };
    }

    public DocumentSuggestionExceptionType getType() {
        return type;
    }
}
