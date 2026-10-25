package com.credit.bladi.model.auth;

import java.util.Objects;

public record RefreshToken(String token, Long userId, long expiresAt, boolean revoked) {
    public RefreshToken {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token cannot be null or blank");
        }
        Objects.requireNonNull(userId, "userId cannot be null");
    }

    public boolean isExpiredAt(long nowMillis) {
        return expiresAt < nowMillis;
    }
}
