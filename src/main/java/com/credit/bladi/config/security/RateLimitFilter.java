package com.credit.bladi.config.security;

import com.credit.bladi.config.web.ClientIpResolver;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import tools.jackson.databind.ObjectMapper;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LogManager.getLogger(RateLimitFilter.class);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final Set<String> RATE_LIMITED_OPERATIONS = Set.of(
        "com.credit.bladi.controller.auth.AuthController#register",
        "com.credit.bladi.controller.auth.AuthController#login",
        "com.credit.bladi.controller.auth.AuthController#refresh",
        "com.credit.bladi.controller.auth.AuthController#logout",
        "com.credit.bladi.controller.mortgage.MortgageCreditController#simulate",
        "com.credit.bladi.controller.investment.InvestmentCreditController#simulate"
    );

    private final Map<String, Integer> rateLimitedPaths;
    private final RateLimitStore rateLimitStore;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final ObjectMapper objectMapper;

    private final Clock clock;

    public RateLimitFilter(
            RateLimitStore rateLimitStore,
            MessageSource messageSource,
            LocaleResolver localeResolver,
            ObjectMapper objectMapper,
            @Value("${security.rate-limit.auth.max-requests:5}") int maxAuthRequests,
            @Value("${security.rate-limit.mortgage-credit.max-requests:20}") int maxCreditSimulationRequests,
            @Value("${security.rate-limit.refresh.max-requests:10}") int maxRefreshRequests,
            @Value("${security.rate-limit.logout.max-requests:10}") int maxLogoutRequests,
            Clock clock) {
        this.clock = clock;
        this.rateLimitStore = rateLimitStore;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.objectMapper = objectMapper;
        this.rateLimitedPaths = Map.of(
            "/api/v1/auth/login", maxAuthRequests,
            "/api/v1/auth/register", maxAuthRequests,
            "/api/v1/auth/refresh", maxRefreshRequests,
            "/api/v1/auth/logout", maxLogoutRequests,
            "/api/v1/mortgage-credit", maxCreditSimulationRequests,
            "/api/v1/investment-credit", maxCreditSimulationRequests
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
            sendRateLimitedResponse(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static String normalize(String path) {
        return path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private void sendRateLimitedResponse(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Locale locale = localeResolver.resolveLocale(request);
        String description = messageSource.getMessage("security.rateLimited", null, locale);
        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(
            UUID.randomUUID().toString(),
            LocalDateTime.now(clock).format(FORMATTER),
            "CB-429",
            description
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
