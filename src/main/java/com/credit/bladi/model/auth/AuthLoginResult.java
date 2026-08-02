package com.credit.bladi.model.auth;

public record AuthLoginResult(String accessToken, String refreshToken, String username, String role) {}