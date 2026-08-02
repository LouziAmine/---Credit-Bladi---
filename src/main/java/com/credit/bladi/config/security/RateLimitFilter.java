package com.credit.bladi.config.security;

import com.credit.bladi.config.web.ClientIpResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(RateLimitFilter.class);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, Integer> rateLimitedPaths;
    private final RateLimitStore rateLimitStore;

    public RateLimitFilter(
            RateLimitStore rateLimitStore,
            @Value("${security.rate-limit.auth.max-requests:5}") int maxAuthRequests,
            @Value("${security.rate-limit.simulation.max-requests:20}") int maxSimulationRequests,
            @Value("${security.rate-limit.refresh.max-requests:10}") int maxRefreshRequests,
            @Value("${security.rate-limit.logout.max-requests:10}") int maxLogoutRequests) {
        this.rateLimitStore = rateLimitStore;
        this.rateLimitedPaths = Map.of(
            "/api/v1/auth/login", maxAuthRequests,
            "/api/v1/auth/register", maxAuthRequests,
            "/api/v1/auth/refresh", maxRefreshRequests,
            "/api/v1/auth/logout", maxLogoutRequests,
            "/api/v1/simulation", maxSimulationRequests
        );
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = normalize(request.getRequestURI());
        Integer maxRequests = rateLimitedPaths.get(path);
        if (maxRequests == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = ClientIpResolver.resolve(request);
        String key = path + ":" + clientIp;

        if (rateLimitStore.isRateLimited(key, maxRequests)) {
            LOGGER.warn("Rate limit exceeded for [{}] on path [{}]", clientIp, path);
            sendRateLimitedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static String normalize(String path) {
        return path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private void sendRateLimitedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String json = String.format(
            "{\"requestId\":\"%s\",\"timestamp\":\"%s\",\"errorCode\":\"CB-429\","
                + "\"description\":\"Too many requests. Please try again in 1 minute.\"}",
            UUID.randomUUID(),
            LocalDateTime.now().format(FORMATTER)
        );
        response.getWriter().write(json);
    }

}