package com.credit.bladi.exception;

public class ClientInvitationException extends ApiException {
    private final ClientInvitationExceptionType type;

    public enum ClientInvitationExceptionType {
        TOKEN_NOT_FOUND,
        TOKEN_EXPIRED
    }

    private ClientInvitationException(ClientInvitationExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static ClientInvitationException of(ClientInvitationExceptionType type) {
        return switch (type) {
            case TOKEN_NOT_FOUND ->
                new ClientInvitationException(type, ApiStatus.NOT_FOUND, "clientInvitation.tokenNotFound");
            case TOKEN_EXPIRED ->
                new ClientInvitationException(type, ApiStatus.CONFLICT, "clientInvitation.tokenExpired");
        };
    }

    public ClientInvitationExceptionType getType() {
        return type;
    }
}
