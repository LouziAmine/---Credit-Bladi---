package com.credit.bladi.exception;

public class AuthException extends ApiException {
    private final AuthExceptionType type;

    private final String detail;

    public enum AuthExceptionType {
        INVALID_CREDENTIALS,
        USERNAME_TAKEN,
        USER_NOT_FOUND,
        SELF_ROLE_MODIFICATION_FORBIDDEN,
        SELF_PROFILE_MODIFICATION_FORBIDDEN,
        LAST_MANAGER_DEMOTION_FORBIDDEN,
        INVALID_REFRESH_TOKEN,
        REFRESH_TOKEN_EXPIRED,
        REFRESH_TOKEN_REUSE_DETECTED,
        ACCOUNT_LOCKED,
        NO_GRANTED_AUTHORITY
    }

    private AuthException(AuthExceptionType type, ApiStatus status, String detail, String messageKey,
            Object... args) {
        super(status, messageKey, args);
        this.type = type;
        this.detail = detail;
    }

    private AuthException(AuthExceptionType type, ApiStatus status, String detail, Throwable cause,
            String messageKey, Object... args) {
        super(status, messageKey, cause, args);
        this.type = type;
        this.detail = detail;
    }

    public static AuthException of(AuthExceptionType type) {
        return of(type, (String) null);
    }

    public static AuthException of(AuthExceptionType type, Throwable cause) {
        AuthException e = of(type);
        return new AuthException(e.type, e.getStatusCode(), e.detail, cause, e.getMessageKey(), e.getArgs());
    }

    public static AuthException of(AuthExceptionType type, String detail) {
        return switch (type) {
            case INVALID_CREDENTIALS ->
                new AuthException(type, ApiStatus.UNAUTHORIZED, detail, "auth.invalidCredentials");
            case USERNAME_TAKEN -> new AuthException(type, ApiStatus.CONFLICT, detail, "auth.usernameTaken");
            case USER_NOT_FOUND -> new AuthException(type, ApiStatus.NOT_FOUND, detail, "auth.userNotFound");
            case SELF_ROLE_MODIFICATION_FORBIDDEN -> new AuthException(type, ApiStatus.FORBIDDEN, detail,
                "auth.selfRoleModificationForbidden");
            case SELF_PROFILE_MODIFICATION_FORBIDDEN -> new AuthException(type, ApiStatus.FORBIDDEN, detail,
                "auth.selfProfileModificationForbidden");
            case LAST_MANAGER_DEMOTION_FORBIDDEN -> new AuthException(type, ApiStatus.FORBIDDEN, detail,
                "auth.lastManagerDemotionForbidden");
            case INVALID_REFRESH_TOKEN ->
                new AuthException(type, ApiStatus.UNAUTHORIZED, detail, "auth.invalidRefreshToken");
            case REFRESH_TOKEN_EXPIRED ->
                new AuthException(type, ApiStatus.UNAUTHORIZED, detail, "auth.refreshTokenExpired");
            case REFRESH_TOKEN_REUSE_DETECTED ->
                new AuthException(type, ApiStatus.UNAUTHORIZED, detail, "auth.refreshTokenReuseDetected");
            case ACCOUNT_LOCKED -> new AuthException(type, ApiStatus.LOCKED, detail, "auth.accountLocked");
            case NO_GRANTED_AUTHORITY -> new AuthException(type, ApiStatus.INTERNAL_SERVER_ERROR, detail,
                "auth.noGrantedAuthority", detail != null ? detail : "unknown");
        };
    }

    public AuthExceptionType getType() {
        return type;
    }
}
