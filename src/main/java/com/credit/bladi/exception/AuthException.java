package com.credit.bladi.exception;

public class AuthException extends ApiException {

    private final AuthExceptionType type;

    private final String detail;

    public enum AuthExceptionType {
        INVALID_CREDENTIALS,
        USERNAME_TAKEN,
        USER_NOT_FOUND,
        SELF_ROLE_MODIFICATION_FORBIDDEN,
        LAST_MANAGER_DEMOTION_FORBIDDEN,
        INVALID_REFRESH_TOKEN,
        REFRESH_TOKEN_EXPIRED,
        REFRESH_TOKEN_REUSE_DETECTED,
        ACCOUNT_LOCKED,
        NO_GRANTED_AUTHORITY
    }

    private AuthException(AuthExceptionType type, ApiStatus status, String detail) {
        super(status);
        this.type = type;
        this.detail = detail;
    }

    public static AuthException of(AuthExceptionType type) {
        return of(type, null);
    }

    public static AuthException of(AuthExceptionType type, String detail) {
        return switch (type) {
            case INVALID_CREDENTIALS -> new AuthException(type, ApiStatus.UNAUTHORIZED, detail);
            case USERNAME_TAKEN -> new AuthException(type, ApiStatus.CONFLICT, detail);
            case USER_NOT_FOUND -> new AuthException(type, ApiStatus.NOT_FOUND, detail);
            case SELF_ROLE_MODIFICATION_FORBIDDEN,
                 LAST_MANAGER_DEMOTION_FORBIDDEN -> new AuthException(type, ApiStatus.FORBIDDEN, detail);
            case INVALID_REFRESH_TOKEN -> new AuthException(type, ApiStatus.UNAUTHORIZED, detail);
            case REFRESH_TOKEN_EXPIRED -> new AuthException(type, ApiStatus.UNAUTHORIZED, detail);
            case REFRESH_TOKEN_REUSE_DETECTED -> new AuthException(type, ApiStatus.UNAUTHORIZED, detail);
            case ACCOUNT_LOCKED -> new AuthException(type, ApiStatus.LOCKED, detail);
            case NO_GRANTED_AUTHORITY -> new AuthException(type, ApiStatus.INTERNAL_SERVER_ERROR, detail);
        };
    }

    public AuthExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return switch (type) {
            case INVALID_CREDENTIALS -> "Invalid username or password.";
            case USERNAME_TAKEN -> "Username is already taken.";
            case USER_NOT_FOUND -> "User not found.";
            case SELF_ROLE_MODIFICATION_FORBIDDEN -> "A manager cannot modify their own role.";
            case LAST_MANAGER_DEMOTION_FORBIDDEN ->
                "Cannot change the role of the last remaining manager. Promote another user to "
                    + "manager first.";
            case INVALID_REFRESH_TOKEN -> "Invalid or missing refresh token.";
            case REFRESH_TOKEN_EXPIRED -> "Refresh token has expired. Please log in again.";
            case REFRESH_TOKEN_REUSE_DETECTED ->
                "Refresh token reuse detected. All sessions have been revoked for security reasons. "
                    + "Please log in again.";
            case ACCOUNT_LOCKED ->
                "Account temporarily locked due to too many failed login attempts. "
                    + "Please try again in 15 minutes.";
            case NO_GRANTED_AUTHORITY ->
                "Authenticated principal '" + (detail != null ? detail : "unknown") + "' has no granted authority.";
        };
    }
}