package com.credit.bladi.unittest.mock.auth;

import com.credit.bladi.controller.dto.auth.request.LoginRequestDTO;
import com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.model.auth.AuthLoginResult;

public final class MockAuth {

    public static final String USERNAME = "john.doe";
    public static final String PASSWORD = "secureP@ss1";
    public static final String ACCESS_TOKEN = "access-token-xyz";
    public static final String REFRESH_TOKEN = "refresh-token-xyz";
    public static final String ROLE = "CLIENT";

    private MockAuth() {}

    public static LoginRequestDTO loginRequest() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        return dto;
    }

    public static RegisterRequestDTO registerRequest() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        return dto;
    }

    public static AuthLoginResult loginResult() {
        return new AuthLoginResult(ACCESS_TOKEN, REFRESH_TOKEN, USERNAME, ROLE);
    }

    public static AuthUserResponseDTO authUserResponse() {
        return new AuthUserResponseDTO(USERNAME, ROLE);
    }
}