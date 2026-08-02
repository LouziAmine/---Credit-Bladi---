package com.credit.bladi.unittest.controller.auth;

import static com.credit.bladi.unittest.mock.auth.MockAuth.ROLE;
import static com.credit.bladi.unittest.mock.auth.MockAuth.USERNAME;
import static com.credit.bladi.unittest.mock.auth.MockAuth.loginRequest;
import static com.credit.bladi.unittest.mock.auth.MockAuth.loginResult;
import static com.credit.bladi.unittest.mock.auth.MockAuth.registerRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.CookieTokenHelper;
import com.credit.bladi.controller.auth.AuthController;
import com.credit.bladi.controller.dto.auth.response.AuthUserResponseDTO;
import com.credit.bladi.service.auth.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    @Mock
    private CookieTokenHelper cookieTokenHelper;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    private AuthController controller;

    @BeforeEach
    void init() {
        controller = new AuthController(authService, cookieTokenHelper);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturn201WhenRegistrationSucceeds() {
        doNothing().when(authService).register(any());

        ResponseEntity<Void> result = controller.register(registerRequest());

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(authService).register(any());
    }

    @Test
    void shouldReturn200WithBodyOnLogin() {
        ResponseCookie cookie = ResponseCookie.from("access_token", "jwt").build();
        when(authService.login(any())).thenReturn(loginResult());
        when(cookieTokenHelper.getCookieMaxAgeSeconds()).thenReturn(3600);
        when(cookieTokenHelper.getRefreshCookieMaxAgeSeconds()).thenReturn(604800);
        when(cookieTokenHelper.buildAuthCookie(anyString(), anyInt())).thenReturn(cookie);
        when(cookieTokenHelper.buildRefreshCookie(anyString(), anyInt())).thenReturn(cookie);

        ResponseEntity<AuthUserResponseDTO> result = controller.login(loginRequest(), httpResponse);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(USERNAME, result.getBody().username());
        assertEquals(ROLE, result.getBody().role());
    }

    @Test
    void shouldReturn204OnLogout() {
        ResponseCookie cleared = ResponseCookie.from("access_token", "").build();
        when(cookieTokenHelper.extractToken(httpRequest)).thenReturn("access-token");
        when(cookieTokenHelper.extractRefreshToken(httpRequest)).thenReturn("refresh-token");
        doNothing().when(authService).logout(anyString(), anyString());
        when(cookieTokenHelper.buildAuthCookie("", 0)).thenReturn(cleared);
        when(cookieTokenHelper.buildRefreshCookie("", 0)).thenReturn(cleared);

        ResponseEntity<Void> result = controller.logout(httpRequest, httpResponse);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(authService).logout("access-token", "refresh-token");
    }

    @Test
    void shouldReturn200OnRefresh() {
        ResponseCookie cookie = ResponseCookie.from("access_token", "new-jwt").build();
        when(cookieTokenHelper.extractRefreshToken(httpRequest)).thenReturn("old-refresh");
        when(authService.refresh("old-refresh")).thenReturn(loginResult());
        when(cookieTokenHelper.getCookieMaxAgeSeconds()).thenReturn(3600);
        when(cookieTokenHelper.getRefreshCookieMaxAgeSeconds()).thenReturn(604800);
        when(cookieTokenHelper.buildAuthCookie(anyString(), anyInt())).thenReturn(cookie);
        when(cookieTokenHelper.buildRefreshCookie(anyString(), anyInt())).thenReturn(cookie);

        ResponseEntity<AuthUserResponseDTO> result = controller.refresh(httpRequest, httpResponse);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(USERNAME, result.getBody().username());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturn200WithAuthenticatedUserInfo() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(USERNAME);
        doReturn((Collection) List.of(new SimpleGrantedAuthority("ROLE_" + ROLE)))
            .when(auth).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(auth);

        ResponseEntity<AuthUserResponseDTO> result = controller.me();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(USERNAME, result.getBody().username());
        assertEquals(ROLE, result.getBody().role());
    }
}