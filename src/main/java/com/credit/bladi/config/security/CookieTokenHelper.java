package com.credit.bladi.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieTokenHelper {
    public static final String ACCESS_COOKIE_NAME = "cb_access_token";
    public static final String REFRESH_COOKIE_NAME = "cb_refresh_token";

    @Value("${security.cookie.secure:true}")
    private boolean secureCookie;

    @Value("${security.cookie.max-age-seconds}")
    private int accessCookieMaxAgeSeconds;

    @Value("${security.refresh-cookie.max-age-seconds}")
    private int refreshCookieMaxAgeSeconds;

    public int getCookieMaxAgeSeconds() {
        return accessCookieMaxAgeSeconds;
    }

    public int getRefreshCookieMaxAgeSeconds() {
        return refreshCookieMaxAgeSeconds;
    }

    public ResponseCookie buildAuthCookie(String value, int maxAge) {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, value)
            .httpOnly(true)
            .path("/")
            .maxAge(maxAge)
            .sameSite("Strict")
            .secure(secureCookie)
            .build();
    }

    public ResponseCookie buildRefreshCookie(String value, int maxAge) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
            .httpOnly(true)
            .path("/api/v1/auth")
            .maxAge(maxAge)
            .sameSite("Strict")
            .secure(secureCookie)
            .build();
    }

    public String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (ACCESS_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
