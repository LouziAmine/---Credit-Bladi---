package com.credit.bladi.config.web;

import com.credit.bladi.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import tools.jackson.databind.ObjectMapper;

public class CachedBodyRequestFilter extends OncePerRequestFilter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String APP_CODE = "CB";

    private final int maxBodySizeBytes;

    private final ObjectMapper objectMapper;

    private final MessageSource messageSource;

    private final LocaleResolver localeResolver;

    private final Clock clock;

    public CachedBodyRequestFilter(final int maxBodySizeBytes, final ObjectMapper objectMapper,
            final MessageSource messageSource, final LocaleResolver localeResolver, final Clock clock) {
        this.clock = clock;
        this.maxBodySizeBytes = maxBodySizeBytes;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @Override
    protected void doFilterInternal(
        final @NonNull HttpServletRequest request,
        final @NonNull HttpServletResponse response,
        final @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request instanceof CachedBodyHttpServletRequest || isMultipart(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            filterChain.doFilter(new CachedBodyHttpServletRequest(request, maxBodySizeBytes), response);
        } catch (RequestBodyTooLargeException e) {
            sendTooLargeResponse(request, response);
        }
    }

    private static boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("multipart/");
    }

    private void sendTooLargeResponse(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(UUID.randomUUID().toString(),
            LocalDateTime.now(clock).format(FORMATTER), APP_CODE + "-413",
            messageSource.getMessage("error.status.413", null, localeResolver.resolveLocale(request)));
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
