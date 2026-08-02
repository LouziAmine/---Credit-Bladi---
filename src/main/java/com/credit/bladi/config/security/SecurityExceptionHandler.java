package com.credit.bladi.config.security;

import com.credit.bladi.config.web.RequestIdValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    private static final String REQUEST_ID_HEADER = "x-request-id";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String FORBIDDEN_MSG = "Access denied for this request. Please check your permissions.";
    private static final String UNAUTHORIZED_MSG = "Authentication required. Please log in.";

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) throws IOException {
        writeError(req, res, HttpServletResponse.SC_FORBIDDEN, "CB-403", FORBIDDEN_MSG);
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
            AuthenticationException e) throws IOException {
        writeError(req, res, HttpServletResponse.SC_UNAUTHORIZED, "CB-401", UNAUTHORIZED_MSG);
    }

    private void writeError(HttpServletRequest req, HttpServletResponse res, int status, String errorCode,
            String description) throws IOException {
        String requestId = req.getHeader(REQUEST_ID_HEADER);
        if (!RequestIdValidator.isValid(requestId)) {
            requestId = UUID.randomUUID().toString();
        }
        String body = String.format(
            "{\"requestId\":\"%s\",\"timestamp\":\"%s\",\"errorCode\":\"%s\",\"description\":\"%s\"}",
            requestId,
            LocalDateTime.now().format(FORMATTER),
            errorCode,
            description
        );
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(body);
    }
}
