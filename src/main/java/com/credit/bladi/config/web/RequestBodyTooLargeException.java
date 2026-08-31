package com.credit.bladi.config.web;

import java.io.IOException;

public class RequestBodyTooLargeException extends IOException {
    public RequestBodyTooLargeException(final int maxBodySizeBytes) {
        super("Request body exceeds maximum allowed size of " + maxBodySizeBytes + " bytes");
    }
}
