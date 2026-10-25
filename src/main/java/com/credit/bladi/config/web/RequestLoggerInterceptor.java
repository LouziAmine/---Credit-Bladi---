package com.credit.bladi.config.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

public class RequestLoggerInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LogManager.getLogger(RequestLoggerInterceptor.class);
    private static final String START_TIME_ATTR = "requestStartNanos";

    private static final Pattern UNSAFE_LOG_CHARS = Pattern.compile("[\\p{Cntrl}\\u2028\\u2029]");

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/actuator")) {
            request.setAttribute(START_TIME_ATTR, System.nanoTime());
            LOGGER.info("INCOMING REQUEST : METHOD={}, URI={}, QUERY={}",
                request.getMethod(), uri, sanitize(request.getQueryString()));
        }
        return true;
    }

    private static String sanitize(String value) {
        return value == null ? null : UNSAFE_LOG_CHARS.matcher(value).replaceAll("");
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/actuator")) {
            Object startAttr = request.getAttribute(START_TIME_ATTR);
            long durationMs = startAttr instanceof Long start
                ? (System.nanoTime() - start) / 1_000_000
                : -1;
            LOGGER.info("OUTGOING RESPONSE : METHOD={}, URI={}, STATUS={}, DURATION={}ms",
                request.getMethod(), uri, response.getStatus(), durationMs);
        }
    }
}
