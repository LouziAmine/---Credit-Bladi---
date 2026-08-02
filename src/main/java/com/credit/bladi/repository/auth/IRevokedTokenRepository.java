package com.credit.bladi.repository.auth;

public interface IRevokedTokenRepository {

    void revoke(String token, long expiresAt);

    boolean isRevoked(String token);

    int deleteExpiredTokens();
}
