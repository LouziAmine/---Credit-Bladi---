package com.credit.bladi.unittest.config.security;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ROLE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TESTSECRETKEYFORUNITTESTINGABC12;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_BAD_TOKEN_HERE;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_SOME_TOKEN;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_CREDS;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_PASS;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ALICE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_BOB;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.config.security.CookieTokenHelper;
import com.credit.bladi.config.security.JwtAuthFilter;
import com.credit.bladi.infrastructure.security.JwtService;
import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import com.credit.bladi.service.auth.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
        .encodeToString(LABEL_TESTSECRETKEYFORUNITTESTINGABC12.getBytes(StandardCharsets.UTF_8));

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
        jwtService = new JwtService(SECRET, 3_600_000L, revokedTokenRepository, Clock.systemUTC());
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
        UserDetails userDetails = new User(USER_ALICE, PASSWORD_PASS,
            List.of(new SimpleGrantedAuthority(CODE_ROLE_CLIENT)));
        String token = jwtService.generateToken(USER_ALICE, CODE_CLIENT);
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(token);
        when(userDetailsService.loadUserByUsername(USER_ALICE)).thenReturn(userDetails);
        when(revokedTokenRepository.isRevoked(token)).thenReturn(false);

        filter.doFilter(mockRequest, mockResponse, filterChain);

        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldContinueChainWhenTokenInvalid() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(TOKEN_BAD_TOKEN_HERE);

        filter.doFilter(mockRequest, mockResponse, filterChain);

        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotOverwriteExistingAuthentication() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        String token = jwtService.generateToken(USER_ALICE, CODE_CLIENT);
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(token);
        org.springframework.security.authentication.TestingAuthenticationToken existingAuth =
            new org.springframework.security.authentication.TestingAuthenticationToken(USER_BOB, PASSWORD_CREDS);
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
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(TOKEN_SOME_TOKEN);
        when(mockTokenService.findUsername(TOKEN_SOME_TOKEN)).thenReturn(Optional.empty());

        filterWithMockTokenService.doFilter(mockRequest, mockResponse, filterChain);

        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void shouldNotSetAuthenticationWhenTokenIsRevoked() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        UserDetails userDetails = new User(USER_ALICE, PASSWORD_PASS,
            List.of(new SimpleGrantedAuthority(CODE_ROLE_CLIENT)));
        String token = jwtService.generateToken(USER_ALICE, CODE_CLIENT);
        when(cookieTokenHelper.extractToken(any(HttpServletRequest.class))).thenReturn(token);
        when(userDetailsService.loadUserByUsername(USER_ALICE)).thenReturn(userDetails);
        when(revokedTokenRepository.isRevoked(token)).thenReturn(true);

        filter.doFilter(mockRequest, mockResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(mockRequest, mockResponse);
    }
}
