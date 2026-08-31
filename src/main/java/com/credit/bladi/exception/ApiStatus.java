package com.credit.bladi.exception;

public enum ApiStatus {
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    LOCKED(423),
    UNPROCESSABLE_CONTENT(422),
    INTERNAL_SERVER_ERROR(500),
    SERVICE_UNAVAILABLE(503);

    private final int code;

    ApiStatus(int code) {
        this.code = code;
    }

    public int value() {
        return code;
    }
}
