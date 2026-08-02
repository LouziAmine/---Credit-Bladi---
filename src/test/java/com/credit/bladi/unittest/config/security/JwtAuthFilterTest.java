package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.CookieTokenHelper;
import com.credit.bladi.config.security.JwtAuthFilter;
import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import com.credit.bladi.service.auth.ITokenService;
import com.credit.bladi.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    private static final String SECRET = Base64.getEncoder()
        .encodeToString("TestSecretKeyForUnitTestingABC12".getBytes(StandardCharsets.UTF_8));

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private CookieTokenHelper cookieTokenHelper;

    @Mock
    private FilterChain filterChain;

    @Mock
    private IRevokedTokenRepository revokedTokenRepository;

    private JwtService jwtService;

    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L, revokedTokenRepository);
        filter = new JwtAuthFilter(jwtService, userDetailsService, cookieTokenHelper);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPassFilterWhenNoTokenPresent() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(null);

        filter.doFilter(mockRequest, mockResponse, filterChain);

        verify(filterChain).doFilter(mockRequest, mockResponse);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void shouldSetAuthenticationWhenTokenIsValid() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        UserDetails userDetails = new User("alice", "pass",
            List.of(new SimpleGrantedAuthority("ROLE_CLIENT")));
        String token = jwtService.generateToken("alice", "CLIENT");
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(token);
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(revokedTokenRepository.isRevoked(token)).thenReturn(false);

        filter.doFilter(mockRequest, mockResponse, filterChain);

        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldContinueChainWhenTokenInvalid() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn("bad.token.here");

        filter.doFilter(mockRequest, mockResponse, filterChain);

        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotOverwriteExistingAuthentication() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        String token = jwtService.generateToken("alice", "CLIENT");
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(token);
        org.springframework.security.authentication.TestingAuthenticationToken existingAuth =
            new org.springframework.security.authentication.TestingAuthenticationToken("bob", "creds");
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        filter.doFilter(mockRequest, mockResponse, filterChain);

        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotSetAuthenticationWhenExtractedUsernameIsNull() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        ITokenService mockTokenService = org.mockito.Mockito.mock(ITokenService.class);
        JwtAuthFilter filterWithMockTokenService =
            new JwtAuthFilter(mockTokenService, userDetailsService, cookieTokenHelper);
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn("some-token");
        when(mockTokenService.extractUsername("some-token")).thenReturn(null);

        filterWithMockTokenService.doFilter(mockRequest, mockResponse, filterChain);

        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotSetAuthenticationWhenTokenIsRevoked() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        UserDetails userDetails = new User("alice", "pass",
            List.of(new SimpleGrantedAuthority("ROLE_CLIENT")));
        String token = jwtService.generateToken("alice", "CLIENT");
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(token);
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(revokedTokenRepository.isRevoked(token)).thenReturn(true);

        filter.doFilter(mockRequest, mockResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(mockRequest, mockResponse);
    }
}