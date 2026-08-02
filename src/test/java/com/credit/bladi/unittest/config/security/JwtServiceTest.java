package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import com.credit.bladi.service.auth.JwtService;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
        .encodeToString("TestSecretKeyForUnitTestingABC12".getBytes(StandardCharsets.UTF_8));

    @Mock
    private IRevokedTokenRepository revokedTokenRepository;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L, revokedTokenRepository);
    }

    @Test
    void shouldGenerateNonNullToken() {
        String token = jwtService.generateToken("testuser", "CLIENT");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken("testuser", "CLIENT");
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("testuser", "CLIENT");
        when(revokedTokenRepository.isRevoked(anyString())).thenReturn(false);

        assertTrue(jwtService.isTokenValid(token, "testuser"));
    }

    @Test
    void shouldReturnFalseForRevokedToken() {
        String token = jwtService.generateToken("testuser", "CLIENT");
        when(revokedTokenRepository.isRevoked(anyString())).thenReturn(true);

        assertFalse(jwtService.isTokenValid(token, "testuser"));
    }

    @Test
    void shouldBlacklistToken() {
        String token = jwtService.generateToken("testuser", "CLIENT");

        jwtService.blacklistToken(token);

        when(revokedTokenRepository.isRevoked(token)).thenReturn(true);
        assertFalse(jwtService.isTokenValid(token, "testuser"));
    }

    @Test
    void shouldNotThrowWhenBlacklistingInvalidToken() {
        jwtService.blacklistToken("invalid.token.here");
    }

    @Test
    void shouldReturnFalseForExpiredToken() throws Exception {
        String token = jwtService.generateToken("testuser", "CLIENT");

        Constructor<JwtService> constructor = JwtService.class.getDeclaredConstructor(
            String.class, long.class, IRevokedTokenRepository.class, Clock.class);
        constructor.setAccessible(true);
        Clock futureClock = Clock.fixed(Instant.now().plusSeconds(10 * 365 * 24 * 3600L), ZoneOffset.UTC);
        JwtService expiredAwareJwtService = constructor.newInstance(SECRET, 3_600_000L, revokedTokenRepository,
            futureClock);
        when(revokedTokenRepository.isRevoked(anyString())).thenReturn(false);

        assertFalse(expiredAwareJwtService.isTokenValid(token, "testuser"));
    }

    @Test
    void shouldReturnFalseForWrongUser() {
        String token = jwtService.generateToken("testuser", "CLIENT");
        when(revokedTokenRepository.isRevoked(anyString())).thenReturn(false);

        assertFalse(jwtService.isTokenValid(token, "other"));
    }

    @Test
    void shouldGenerateDistinctTokensForSameUserAndRoleWithinTheSameSecond() {
        String first = jwtService.generateToken("testuser", "CLIENT");
        String second = jwtService.generateToken("testuser", "CLIENT");

        assertNotEquals(first, second);
    }
}