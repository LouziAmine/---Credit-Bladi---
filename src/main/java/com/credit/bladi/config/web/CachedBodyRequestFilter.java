package com.credit.bladi.config.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class CachedBodyRequestFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final int maxBodySizeBytes;

    public CachedBodyRequestFilter(final int maxBodySizeBytes) {
        this.maxBodySizeBytes = maxBodySizeBytes;
    }

    @Override
    protected void doFilterInternal(
        final @NonNull HttpServletRequest request,
        final @NonNull HttpServletResponse response,
        final @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request instanceof CachedBodyHttpServletRequest) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            filterChain.doFilter(new CachedBodyHttpServletRequest(request, maxBodySizeBytes), response);
        } catch (RequestBodyTooLargeException e) {
            sendTooLargeResponse(response);
        }
    }

    private void sendTooLargeResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String json = String.format(
            "{\"requestId\":\"%s\",\"timestamp\":\"%s\",\"errorCode\":\"CB-413\","
                + "\"description\":\"Request body exceeds the maximum allowed size.\"}",
            UUID.randomUUID(),
            LocalDateTime.now().format(FORMATTER)
        );
        response.getWriter().write(json);
    }
}