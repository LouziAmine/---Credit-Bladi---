package com.credit.bladi.config.security;

import com.credit.bladi.config.web.RequestIdValidator;
import com.credit.bladi.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import tools.jackson.databind.ObjectMapper;

@Component
public class SecurityExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {
    private static final String REQUEST_ID_HEADER = "x-request-id";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MessageSource messageSource;

    private final LocaleResolver localeResolver;

    private final ObjectMapper objectMapper;

    private final Clock clock;

    public SecurityExceptionHandler(MessageSource messageSource, LocaleResolver localeResolver,
            ObjectMapper objectMapper,
            Clock clock) {
        this.clock = clock;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) throws IOException {
        writeError(req, res, HttpServletResponse.SC_FORBIDDEN, "CB-403", localize(req, "error.status.403"));
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res,
            AuthenticationException e) throws IOException {
        writeError(req, res, HttpServletResponse.SC_UNAUTHORIZED, "CB-401", localize(req, "security.unauthorized"));
    }

    private String localize(HttpServletRequest req, String key) {
        Locale locale = localeResolver.resolveLocale(req);
        return messageSource.getMessage(key, null, locale);
    }

    private void writeError(HttpServletRequest req, HttpServletResponse res, int status, String errorCode,
            String description) throws IOException {
        String requestId = req.getHeader(REQUEST_ID_HEADER);
        if (!RequestIdValidator.isValid(requestId)) {
            requestId = UUID.randomUUID().toString();
        }
        ErrorResponse errorResponse = new ErrorResponse(
            requestId,
            LocalDateTime.now(clock).format(FORMATTER),
            errorCode,
            description
        );
        res.setStatus(status);
        res.setCharacterEncoding("UTF-8");
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
