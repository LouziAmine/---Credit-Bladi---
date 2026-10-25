package com.credit.bladi.controller.auth.mapper;

import com.credit.bladi.controller.dto.auth.request.LoginRequestDTO;
import com.credit.bladi.controller.dto.auth.request.RegisterRequestDTO;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.model.auth.AuthLoginResult;
import com.credit.bladi.model.user.User;
import com.credit.bladi.service.auth.AuthorityResolver;
import org.springframework.security.core.Authentication;

public final class AuthControllerMapper {
    private AuthControllerMapper() {
    }

    public static User toUser(RegisterRequestDTO registerRequestDTO) {
        return buildUser(registerRequestDTO.username(), registerRequestDTO.password());
    }

    public static User toUser(LoginRequestDTO loginRequestDTO) {
        return buildUser(loginRequestDTO.username(), loginRequestDTO.password());
    }

    public static AuthUserResponseDTO toAuthUserResponseDTO(AuthLoginResult authLoginResult) {
        return new AuthUserResponseDTO(authLoginResult.username(), authLoginResult.role());
    }

    public static AuthUserResponseDTO toAuthUserResponseDTO(Authentication authentication) {
        return new AuthUserResponseDTO(authentication.getName(), AuthorityResolver.extractRole(authentication));
    }

    private static User buildUser(String username, String password) {
        return User.getBuilder()
            .username(username)
            .password(password)
            .build();
    }
}
