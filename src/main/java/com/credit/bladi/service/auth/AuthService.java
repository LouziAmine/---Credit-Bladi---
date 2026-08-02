package com.credit.bladi.service.auth;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.ACCOUNT_LOCKED;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.INVALID_CREDENTIALS;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.INVALID_REFRESH_TOKEN;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.REFRESH_TOKEN_EXPIRED;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.REFRESH_TOKEN_REUSE_DETECTED;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USERNAME_TAKEN;
import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.model.auth.AuthLoginResult;
import com.credit.bladi.model.auth.RefreshToken;
import com.credit.bladi.model.enums.AuditAction;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.user.User;
import com.credit.bladi.repository.auth.IRefreshTokenRepository;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.audit.IAuditService;
import com.credit.bladi.service.security.ILoginAttemptStore;
import java.util.Optional;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements IAuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthService.class);

    private static final String DUMMY_PASSWORD_HASH =
        "$2a$12$FNu.X4OcbqPjAMFvoI4Z5eAo64vdk7LQZd0jZtsG1e3DvRBoECOyO";

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ITokenService tokenService;

    private final IRefreshTokenRepository refreshTokenRepository;

    private final IAuditService auditService;

    private final ILoginAttemptStore loginAttemptStore;

    private final long refreshExpirationMs;

    public AuthService(IUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ITokenService tokenService,
                       IRefreshTokenRepository refreshTokenRepository,
                       IAuditService auditService,
                       ILoginAttemptStore loginAttemptStore,
                       @Value("${security.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditService = auditService;
        this.loginAttemptStore = loginAttemptStore;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    @Transactional
    public AuthLoginResult login(User credentials) {
        LOGGER.info("Login - username={}", credentials.getUsername());
        String username = credentials.getUsername();
        if (loginAttemptStore.isLocked(username)) {
            auditService.log(username, AuditAction.LOGIN, "LOCKED");
            throw AuthException.of(ACCOUNT_LOCKED);
        }
        Optional<User> found = userRepository.findByUsername(username);
        String hashToVerify = found.map(User::getPassword).orElse(DUMMY_PASSWORD_HASH);
        boolean passwordMatches = passwordEncoder.matches(credentials.getPassword(), hashToVerify);
        if (found.isEmpty() || !passwordMatches) {
            loginAttemptStore.recordFailure(username);
            auditService.log(username, AuditAction.LOGIN, "FAILURE");
            throw AuthException.of(INVALID_CREDENTIALS);
        }
        loginAttemptStore.reset(username);
        User user = found.get();
        String accessToken = tokenService.generateToken(user.getUsername(), user.getRole().name());
        String refreshToken = issueRefreshToken(user.getId());
        auditService.log(user.getUsername(), AuditAction.LOGIN, "SUCCESS role=" + user.getRole().name());
        return new AuthLoginResult(accessToken, refreshToken, user.getUsername(), user.getRole().name());
    }

    @Override
    @Transactional
    public void register(User user) {
        LOGGER.info("Register - username={}", user.getUsername());
        if (userRepository.existsByUsername(user.getUsername())) {
            throw AuthException.of(USERNAME_TAKEN);
        }
        try {
            userRepository.save(buildUser(user, UserRole.CLIENT));
        } catch (DataIntegrityViolationException e) {
            throw AuthException.of(USERNAME_TAKEN);
        }
        auditService.log(user.getUsername(), AuditAction.REGISTER, null);
    }

    @Override
    @Transactional
    public AuthLoginResult refresh(String refreshToken) {
        LOGGER.info("Refresh token");
        if (refreshToken == null) {
            throw AuthException.of(INVALID_REFRESH_TOKEN);
        }
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> AuthException.of(INVALID_REFRESH_TOKEN));

        if (stored.revoked()) {
            String username = userRepository.findById(stored.userId()).map(User::getUsername).orElse(null);
            refreshTokenRepository.revokeAllForUser(stored.userId());
            auditService.log(username, AuditAction.TOKEN_REUSE_DETECTED, "userId=" + stored.userId());
            throw AuthException.of(REFRESH_TOKEN_REUSE_DETECTED);
        }

        if (stored.isExpired()) {
            throw AuthException.of(REFRESH_TOKEN_EXPIRED);
        }

        if (!refreshTokenRepository.revokeIfActive(refreshToken)) {
            String username = userRepository.findById(stored.userId()).map(User::getUsername).orElse(null);
            refreshTokenRepository.revokeAllForUser(stored.userId());
            auditService.log(username, AuditAction.TOKEN_REUSE_DETECTED, "userId=" + stored.userId());
            throw AuthException.of(REFRESH_TOKEN_REUSE_DETECTED);
        }

        User user = userRepository.findById(stored.userId())
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND));

        String newAccessToken = tokenService.generateToken(user.getUsername(), user.getRole().name());
        String newRefreshToken = issueRefreshToken(user.getId());

        auditService.log(user.getUsername(), AuditAction.TOKEN_REFRESH, null);
        return new AuthLoginResult(newAccessToken, newRefreshToken, user.getUsername(), user.getRole().name());
    }

    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        LOGGER.info("Logout");
        if (accessToken != null) {
            tokenService.blacklistToken(accessToken);
        }
        if (refreshToken != null) {
            refreshTokenRepository.revoke(refreshToken);
        }
        String username = accessToken != null ? extractUsernameQuietly(accessToken) : null;
        auditService.log(username, AuditAction.LOGOUT, null);
    }

    private String extractUsernameQuietly(String token) {
        try {
            return tokenService.extractUsername(token);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String issueRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + refreshExpirationMs;
        refreshTokenRepository.save(token, userId, expiresAt);
        return token;
    }

    private User buildUser(User credentials, UserRole role) {
        return User.getBuilder()
            .username(credentials.getUsername())
            .password(passwordEncoder.encode(credentials.getPassword()))
            .role(role)
            .build();
    }
}