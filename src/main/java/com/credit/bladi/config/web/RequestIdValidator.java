package com.credit.bladi.config.web;

import java.util.regex.Pattern;

public final class RequestIdValidator {
    private static final Pattern SAFE_REQUEST_ID = Pattern.compile("^[A-Za-z0-9-]{1,64}$");

    private RequestIdValidator() {
    }

    public static boolean isValid(String requestId) {
        return requestId != null && SAFE_REQUEST_ID.matcher(requestId).matches();
    }
}
