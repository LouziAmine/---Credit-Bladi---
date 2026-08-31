package com.credit.bladi.unittest.mock.auth;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_ACCESS_TOKEN_XYZ;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_REFRESH_TOKEN_XYZ;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_SECUREP_AT_SS1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JOHN_DOE;

import com.credit.bladi.controller.dto.auth.request.LoginRequestDTO;
import com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.model.auth.AuthLoginResult;

public final class MockAuth {
    public static final String USERNAME = USER_JOHN_DOE;
    public static final String PASSWORD = PASSWORD_SECUREP_AT_SS1;
    public static final String ACCESS_TOKEN = TOKEN_ACCESS_TOKEN_XYZ;
    public static final String REFRESH_TOKEN = TOKEN_REFRESH_TOKEN_XYZ;
    public static final String ROLE = CODE_CLIENT;

    private MockAuth() {}

    public static LoginRequestDTO loginRequest() {
        return new LoginRequestDTO(USERNAME, PASSWORD);
    }

    public static RegisterRequestDTO registerRequest() {
        return new RegisterRequestDTO(USERNAME, PASSWORD);
    }

    public static AuthLoginResult loginResult() {
        return new AuthLoginResult(ACCESS_TOKEN, REFRESH_TOKEN, USERNAME, ROLE);
    }

    public static AuthUserResponseDTO authUserResponse() {
        return new AuthUserResponseDTO(USERNAME, ROLE);
    }
}
