package com.credit.bladi.exception;

public class ApiException extends RuntimeException {

    private final ApiStatus statusCode;

    public ApiException(ApiStatus statusCode) {
        this.statusCode = statusCode;
    }

    public ApiStatus getStatusCode() {
        return statusCode;
    }
}
