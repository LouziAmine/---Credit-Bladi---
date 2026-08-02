package com.credit.bladi.service.auth;

public interface ITokenService {

    String generateToken(String username, String role);

    String extractUsername(String token);

    boolean isTokenValid(String token, String username);

    void blacklistToken(String token);
}