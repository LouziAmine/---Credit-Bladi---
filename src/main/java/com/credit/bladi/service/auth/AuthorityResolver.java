package com.credit.bladi.service.auth;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.NO_GRANTED_AUTHORITY;

import com.credit.bladi.exception.AuthException;
import org.springframework.security.core.Authentication;

public final class AuthorityResolver {
    private AuthorityResolver() {
    }

    public static String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .findFirst()
            .map(authority -> authority.getAuthority().replace("ROLE_", ""))
            .orElseThrow(() -> AuthException.of(NO_GRANTED_AUTHORITY, authentication.getName()));
    }
}
