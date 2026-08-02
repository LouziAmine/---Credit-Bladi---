package com.credit.bladi.unittest.controller.auth.mapper;

import static com.credit.bladi.unittest.mock.auth.MockAuth.PASSWORD;
import static com.credit.bladi.unittest.mock.auth.MockAuth.ROLE;
import static com.credit.bladi.unittest.mock.auth.MockAuth.USERNAME;
import static com.credit.bladi.unittest.mock.auth.MockAuth.loginRequest;
import static com.credit.bladi.unittest.mock.auth.MockAuth.registerRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.credit.bladi.controller.auth.mapper.AuthControllerMapper;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.AuthException.AuthExceptionType;
import com.credit.bladi.model.user.User;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class AuthControllerMapperTest {

    @Test
    void shouldMapLoginRequestToUser() {
        User user = AuthControllerMapper.toUser(loginRequest());

        User expected = User.getBuilder().username(USERNAME).password(PASSWORD).build();
        assertTrue(EqualsBuilder.reflectionEquals(expected, user));
    }

    @Test
    void shouldMapRegisterRequestToUser() {
        User user = AuthControllerMapper.toUser(registerRequest());

        User expected = User.getBuilder().username(USERNAME).password(PASSWORD).build();
        assertTrue(EqualsBuilder.reflectionEquals(expected, user));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldMapAuthenticationToResponseDTOAndStripRolePrefix() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(USERNAME);
        doReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_" + ROLE)))
            .when(auth).getAuthorities();

        AuthUserResponseDTO dto = AuthControllerMapper.toAuthUserResponseDTO(auth);

        assertEquals(USERNAME, dto.username());
        assertEquals(ROLE, dto.role());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowWhenAuthenticatedPrincipalHasNoAuthorities() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(USERNAME);
        doReturn((Collection) List.of()).when(auth).getAuthorities();

        AuthException ex = assertThrows(AuthException.class, () -> AuthControllerMapper.toAuthUserResponseDTO(auth));
        assertEquals(AuthExceptionType.NO_GRANTED_AUTHORITY, ex.getType());
    }
}