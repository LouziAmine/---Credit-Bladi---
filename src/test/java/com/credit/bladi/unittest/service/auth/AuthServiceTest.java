package com.credit.bladi.unittest.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.auth.AuthLoginResult;
import com.credit.bladi.model.auth.RefreshToken;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.auth.IRefreshTokenRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.auth.AuthService;
import com.credit.bladi.service.auth.JwtService;
import com.credit.bladi.service.security.ILoginAttemptStore;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private IRefreshTokenRepository refreshTokenRepository;

    @Mock
    private IAuditService auditService;

    @Mock
    private ILoginAttemptStore loginAttemptStore;

    private AuthService authService;

    private User storedUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, refreshTokenRepository,
            auditService, loginAttemptStore, 86400000L);

        storedUser = User.getBuilder()
            .id(1L)
            .username("jdoe")
            .password("$2a$encoded")
            .role(UserRole.CLIENT)
            .build();
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        User credentials = User.getBuilder().username("jdoe").password("secret").build();
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("secret", "$2a$encoded")).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("jwt-token");

        AuthLoginResult result = authService.login(credentials);

        assertEquals("jwt-token", result.accessToken());
        assertEquals("jdoe", result.username());
        assertEquals("CLIENT", result.role());
        verify(loginAttemptStore).reset("jdoe");
    }

    @Test
    void shouldThrowAccountLockedWhenLoginAttemptStoreReportsLocked() {
        User credentials = User.getBuilder().username("jdoe").password("secret").build();
        when(loginAttemptStore.isLocked("jdoe")).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(credentials));

        assertEquals(AuthException.AuthExceptionType.ACCOUNT_LOCKED, exception.getType());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void shouldThrowAuthExceptionWhenUserNotFound() {
        User credentials = User.getBuilder().username("unknown").password("secret").build();
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.login(credentials));
    }

    @Test
    void shouldThrowAuthExceptionWhenPasswordDoesNotMatch() {
        User credentials = User.getBuilder().username("jdoe").password("wrong").build();
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("wrong", "$2a$encoded")).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.login(credentials));

        verify(loginAttemptStore).recordFailure("jdoe");
    }

    @Test
    void shouldEncodePasswordAndSaveUserOnRegister() {
        User user = User.getBuilder().username("newuser").password("plain").build();
        when(passwordEncoder.encode("plain")).thenReturn("$2a$hashed");

        authService.register(user);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("plain");
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateUsername() {
        User user = User.getBuilder().username("jdoe").password("plain").build();
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThrows(AuthException.class, () -> authService.register(user));
    }

    @Test
    void shouldThrowWhenRegisterHitsUniqueConstraintRace() {
        User user = User.getBuilder().username("newuser").password("plain").build();
        when(passwordEncoder.encode("plain")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        assertThrows(AuthException.class, () -> authService.register(user));
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken stored = new RefreshToken("old-refresh", 1L, futureExpiry, false);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.revokeIfActive("old-refresh")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(storedUser));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("new-jwt");

        AuthLoginResult result = authService.refresh("old-refresh");

        assertEquals("new-jwt", result.accessToken());
        verify(refreshTokenRepository).revokeIfActive("old-refresh");
        verify(refreshTokenRepository).save(anyString(), anyLong(), anyLong());
    }

    @Test
    void shouldRevokeAllSessionsAndThrowWhenConcurrentRefreshLosesRotationRace() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken stored = new RefreshToken("old-refresh", 1L, futureExpiry, false);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.revokeIfActive("old-refresh")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(storedUser));

        AuthException exception = assertThrows(AuthException.class, () -> authService.refresh("old-refresh"));

        assertEquals(AuthException.AuthExceptionType.REFRESH_TOKEN_REUSE_DETECTED, exception.getType());
        verify(refreshTokenRepository).revokeAllForUser(1L);
    }

    @Test
    void shouldThrowWhenRefreshTokenIsNull() {
        assertThrows(AuthException.class, () -> authService.refresh(null));
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        when(refreshTokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.refresh("bad-token"));
    }

    @Test
    void shouldThrowWhenRefreshedUserNoLongerExists() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken stored = new RefreshToken("old-refresh", 1L, futureExpiry, false);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.revokeIfActive("old-refresh")).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.refresh("old-refresh"));
    }

    @Test
    void shouldThrowWhenRefreshTokenExpired() {
        long pastExpiry = System.currentTimeMillis() - 1L;
        RefreshToken expired = new RefreshToken("old-refresh", 1L, pastExpiry, false);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(expired));

        assertThrows(AuthException.class, () -> authService.refresh("old-refresh"));
    }

    @Test
    void shouldRevokeAllSessionsAndThrowWhenRefreshTokenReused() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken revoked = new RefreshToken("old-refresh", 1L, futureExpiry, true);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(revoked));
        when(userRepository.findById(1L)).thenReturn(Optional.of(storedUser));

        AuthException exception = assertThrows(AuthException.class, () -> authService.refresh("old-refresh"));

        assertEquals(AuthException.AuthExceptionType.REFRESH_TOKEN_REUSE_DETECTED, exception.getType());
        verify(refreshTokenRepository).revokeAllForUser(1L);
    }

    @Test
    void shouldBlacklistAccessTokenAndRevokeRefreshTokenOnLogout() {
        authService.logout("some-access-token", "some-refresh-token");

        verify(jwtService).blacklistToken("some-access-token");
        verify(refreshTokenRepository).revoke("some-refresh-token");
    }

    @Test
    void shouldHandleLogoutWithNullTokens() {
        authService.logout(null, null);
    }

    @Test
    void shouldAuditLogoutWithNullUsernameWhenAccessTokenIsUnparseable() {
        when(jwtService.extractUsername("malformed-token")).thenThrow(new RuntimeException("malformed"));

        authService.logout("malformed-token", "some-refresh-token");

        verify(auditService).log(org.mockito.ArgumentMatchers.isNull(),
            org.mockito.ArgumentMatchers.eq(com.credit.bladi.model.enums.AuditAction.LOGOUT),
            org.mockito.ArgumentMatchers.isNull());
    }
}