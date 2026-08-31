package com.credit.bladi.unittest.service.auth;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_203_0_113_1;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_BAD_TOKEN;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_JWT_TOKEN;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_MALFORMED_TOKEN;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_NEW_JWT;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_OLD_REFRESH;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_SOME_ACCESS_TOKEN;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_SOME_REFRESH_TOKEN;
import static com.credit.bladi.unittest.mock.data.MockUsers.PASSWORD_SECRET;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_NEWUSER;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.infrastructure.security.JwtService;
import com.credit.bladi.model.auth.AuthLoginResult;
import com.credit.bladi.model.auth.RefreshToken;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.DuplicateEntryException;
import com.credit.bladi.repository.auth.IRefreshTokenRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.auth.AuthService;
import com.credit.bladi.service.request.IRequestContext;
import com.credit.bladi.service.security.ILoginAttemptStore;
import java.time.Clock;
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

    @Mock
    private IRequestContext requestContext;

    private AuthService authService;

    private User storedUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, refreshTokenRepository,
            auditService, loginAttemptStore, 86400000L, requestContext, Clock.systemUTC());

        storedUser = User.getBuilder()
            .id(1L)
            .username(USER_JDOE)
            .password("$2a$encoded")
            .role(UserRole.CLIENT)
            .build();
    }

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        User credentials = User.getBuilder().username(USER_JDOE).password(PASSWORD_SECRET).build();
        when(userRepository.findByUsername(USER_JDOE)).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(PASSWORD_SECRET, "$2a$encoded")).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn(TOKEN_JWT_TOKEN);

        AuthLoginResult result = authService.login(credentials, IP_203_0_113_1);

        assertEquals(TOKEN_JWT_TOKEN, result.accessToken());
        assertEquals(USER_JDOE, result.username());
        assertEquals(CODE_CLIENT, result.role());
        verify(loginAttemptStore).reset(USER_JDOE, IP_203_0_113_1);
    }

    @Test
    void shouldThrowAccountLockedWhenLoginAttemptStoreReportsLocked() {
        User credentials = User.getBuilder().username(USER_JDOE).password(PASSWORD_SECRET).build();
        when(loginAttemptStore.isLocked(USER_JDOE, IP_203_0_113_1)).thenReturn(true);

        AuthException exception = assertThrows(AuthException.class,
            () -> authService.login(credentials, IP_203_0_113_1));

        assertEquals(AuthException.AuthExceptionType.ACCOUNT_LOCKED, exception.getType());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void shouldThrowAuthExceptionWhenUserNotFound() {
        User credentials = User.getBuilder().username(USER_UNKNOWN).password(PASSWORD_SECRET).build();
        when(userRepository.findByUsername(USER_UNKNOWN)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.login(credentials, IP_203_0_113_1));
    }

    @Test
    void shouldThrowAuthExceptionWhenPasswordDoesNotMatch() {
        User credentials = User.getBuilder().username(USER_JDOE).password("wrong").build();
        when(userRepository.findByUsername(USER_JDOE)).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("wrong", "$2a$encoded")).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.login(credentials, IP_203_0_113_1));

        verify(loginAttemptStore).recordFailure(USER_JDOE, IP_203_0_113_1);
    }

    @Test
    void shouldEncodePasswordAndSaveUserOnRegister() {
        User user = User.getBuilder().username(USER_NEWUSER).password("plain").build();
        when(passwordEncoder.encode("plain")).thenReturn("$2a$hashed");

        authService.register(user);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("plain");
    }

    @Test
    void shouldThrowWhenRegisteringDuplicateUsername() {
        User user = User.getBuilder().username(USER_JDOE).password("plain").build();
        when(userRepository.existsByUsername(USER_JDOE)).thenReturn(true);

        assertThrows(AuthException.class, () -> authService.register(user));
    }

    @Test
    void shouldThrowWhenRegisterHitsUniqueConstraintRace() {
        User user = User.getBuilder().username(USER_NEWUSER).password("plain").build();
        when(passwordEncoder.encode("plain")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class)))
            .thenThrow(new DuplicateEntryException(new DataIntegrityViolationException("duplicate")));

        assertThrows(AuthException.class, () -> authService.register(user));
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken stored = new RefreshToken(TOKEN_OLD_REFRESH, 1L, futureExpiry, false);
        when(refreshTokenRepository.findByToken(TOKEN_OLD_REFRESH)).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.revokeIfActive(TOKEN_OLD_REFRESH)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(storedUser));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn(TOKEN_NEW_JWT);

        AuthLoginResult result = authService.refresh(TOKEN_OLD_REFRESH);

        assertEquals(TOKEN_NEW_JWT, result.accessToken());
        verify(refreshTokenRepository).revokeIfActive(TOKEN_OLD_REFRESH);
        verify(refreshTokenRepository).save(anyString(), anyLong(), anyLong());
    }

    @Test
    void shouldRevokeAllSessionsAndThrowWhenConcurrentRefreshLosesRotationRace() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken stored = new RefreshToken(TOKEN_OLD_REFRESH, 1L, futureExpiry, false);
        when(refreshTokenRepository.findByToken(TOKEN_OLD_REFRESH)).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.revokeIfActive(TOKEN_OLD_REFRESH)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(storedUser));

        AuthException exception = assertThrows(AuthException.class, () -> authService.refresh(TOKEN_OLD_REFRESH));

        assertEquals(AuthException.AuthExceptionType.REFRESH_TOKEN_REUSE_DETECTED, exception.getType());
        verify(refreshTokenRepository).revokeAllForUser(1L);
    }

    @Test
    void shouldThrowWhenRefreshTokenIsNull() {
        assertThrows(AuthException.class, () -> authService.refresh(null));
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        when(refreshTokenRepository.findByToken(TOKEN_BAD_TOKEN)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.refresh(TOKEN_BAD_TOKEN));
    }

    @Test
    void shouldThrowWhenRefreshedUserNoLongerExists() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken stored = new RefreshToken(TOKEN_OLD_REFRESH, 1L, futureExpiry, false);
        when(refreshTokenRepository.findByToken(TOKEN_OLD_REFRESH)).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.revokeIfActive(TOKEN_OLD_REFRESH)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.refresh(TOKEN_OLD_REFRESH));
    }

    @Test
    void shouldThrowWhenRefreshTokenExpired() {
        long pastExpiry = System.currentTimeMillis() - 1L;
        RefreshToken expired = new RefreshToken(TOKEN_OLD_REFRESH, 1L, pastExpiry, false);
        when(refreshTokenRepository.findByToken(TOKEN_OLD_REFRESH)).thenReturn(Optional.of(expired));

        assertThrows(AuthException.class, () -> authService.refresh(TOKEN_OLD_REFRESH));
    }

    @Test
    void shouldRevokeAllSessionsAndThrowWhenRefreshTokenReused() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken revoked = new RefreshToken(TOKEN_OLD_REFRESH, 1L, futureExpiry, true);
        when(refreshTokenRepository.findByToken(TOKEN_OLD_REFRESH)).thenReturn(Optional.of(revoked));
        when(userRepository.findById(1L)).thenReturn(Optional.of(storedUser));

        AuthException exception = assertThrows(AuthException.class, () -> authService.refresh(TOKEN_OLD_REFRESH));

        assertEquals(AuthException.AuthExceptionType.REFRESH_TOKEN_REUSE_DETECTED, exception.getType());
        verify(refreshTokenRepository).revokeAllForUser(1L);
    }

    @Test
    void shouldBlacklistAccessTokenAndRevokeRefreshTokenOnLogout() {
        authService.logout(TOKEN_SOME_ACCESS_TOKEN, TOKEN_SOME_REFRESH_TOKEN);

        verify(jwtService).blacklistToken(TOKEN_SOME_ACCESS_TOKEN);
        verify(refreshTokenRepository).revoke(TOKEN_SOME_REFRESH_TOKEN);
    }

    @Test
    void shouldHandleLogoutWithNullTokens() {
        authService.logout(null, null);
    }

    @Test
    void shouldAuditLogoutWithNullUsernameWhenAccessTokenIsUnparseable() {
        when(jwtService.findUsername(TOKEN_MALFORMED_TOKEN)).thenReturn(Optional.empty());

        authService.logout(TOKEN_MALFORMED_TOKEN, TOKEN_SOME_REFRESH_TOKEN);

        verify(auditService).log(org.mockito.ArgumentMatchers.isNull(),
            org.mockito.ArgumentMatchers.eq(com.credit.bladi.model.enums.AuditAction.LOGOUT),
            org.mockito.ArgumentMatchers.isNull());
    }
}
