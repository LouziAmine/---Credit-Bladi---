package com.credit.bladi.exception;

import java.util.Arrays;

public class ApiException extends RuntimeException {
    private final ApiStatus statusCode;

    private final String messageKey;

    private final Object[] args;

    public ApiException(ApiStatus statusCode, String messageKey, Object... args) {
        super(messageKey);
        this.statusCode = statusCode;
        this.messageKey = messageKey;
        this.args = args;
    }

    protected ApiException(ApiStatus statusCode, String messageKey, Throwable cause, Object... args) {
        super(messageKey, cause);
        this.statusCode = statusCode;
        this.messageKey = messageKey;
        this.args = args;
    }

    @Override
    public String getMessage() {
        return args == null || args.length == 0 ? messageKey : messageKey + " " + Arrays.toString(args);
    }

    public ApiStatus getStatusCode() {
        return statusCode;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
