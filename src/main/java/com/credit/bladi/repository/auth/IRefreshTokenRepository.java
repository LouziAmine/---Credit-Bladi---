package com.credit.bladi.repository.auth;

import com.credit.bladi.model.auth.RefreshToken;
import java.util.Optional;

public interface IRefreshTokenRepository {

    void save(String token, Long userId, long expiresAt);

    Optional<RefreshToken> findByToken(String token);

    void revoke(String token);

    boolean revokeIfActive(String token);

    void revokeAllForUser(Long userId);

    int deleteExpiredTokens();
}