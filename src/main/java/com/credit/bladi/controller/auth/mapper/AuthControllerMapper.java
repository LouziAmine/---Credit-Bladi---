package com.credit.bladi.controller.auth.mapper;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.NO_GRANTED_AUTHORITY;

import com.credit.bladi.controller.dto.auth.request.LoginRequestDTO;
import com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.auth.AuthLoginResult;
import com.credit.bladi.model.user.User;
import org.springframework.security.core.Authentication;

public final class AuthControllerMapper {

    private AuthControllerMapper() {
    }

    public static User toUser(RegisterRequestDTO registerRequestDTO) {
        return buildUser(registerRequestDTO.getUsername(), registerRequestDTO.getPassword());
    }

    public static User toUser(LoginRequestDTO loginRequestDTO) {
        return buildUser(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
    }

    public static AuthUserResponseDTO toAuthUserResponseDTO(AuthLoginResult authLoginResult) {
        return new AuthUserResponseDTO(authLoginResult.username(), authLoginResult.role());
    }

    public static AuthUserResponseDTO toAuthUserResponseDTO(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElseThrow(() -> AuthException.of(NO_GRANTED_AUTHORITY, authentication.getName()));
        return new AuthUserResponseDTO(authentication.getName(), role);
    }

    private static User buildUser(String username, String password) {
        return User.getBuilder()
            .username(username)
            .password(password)
            .build();
    }
}