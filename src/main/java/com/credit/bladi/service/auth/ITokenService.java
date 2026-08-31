package com.credit.bladi.service.auth;

import java.util.Optional;

public interface ITokenService {
    String generateToken(String username, String role);

    Optional<String> findUsername(String token);

    boolean isTokenValid(String token, String username);

    void blacklistToken(String token);
}
