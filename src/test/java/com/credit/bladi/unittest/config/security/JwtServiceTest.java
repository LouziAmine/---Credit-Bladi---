package com.credit.bladi.unittest.config.security;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLIENT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_TESTSECRETKEYFORUNITTESTINGABC12;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_TESTUSER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.credit.bladi.infrastructure.security.JwtService;
import com.credit.bladi.repository.auth.IRevokedTokenRepository;
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
        .encodeToString(LABEL_TESTSECRETKEYFORUNITTESTINGABC12.getBytes(StandardCharsets.UTF_8));

    @Mock
    private IRevokedTokenRepository revokedTokenRepository;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L, revokedTokenRepository, Clock.systemUTC());
    }

    @Test
    void shouldGenerateNonNullToken() {
        String token = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);
        String username = jwtService.findUsername(token).orElseThrow();
        assertEquals(USER_TESTUSER, username);
    }

    @Test
    void shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);
        when(revokedTokenRepository.isRevoked(anyString())).thenReturn(false);

        assertTrue(jwtService.isTokenValid(token, USER_TESTUSER));
    }

    @Test
    void shouldReturnFalseForRevokedToken() {
        String token = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);
        when(revokedTokenRepository.isRevoked(anyString())).thenReturn(true);

        assertFalse(jwtService.isTokenValid(token, USER_TESTUSER));
    }

    @Test
    void shouldBlacklistToken() {
        String token = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);

        jwtService.blacklistToken(token);

        when(revokedTokenRepository.isRevoked(token)).thenReturn(true);
        assertFalse(jwtService.isTokenValid(token, USER_TESTUSER));
    }

    @Test
    void shouldNotThrowWhenBlacklistingInvalidToken() {
        jwtService.blacklistToken("invalid.token.here");
    }

    @Test
    void shouldReturnFalseForExpiredToken() throws Exception {
        String token = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);

        Constructor<JwtService> constructor = JwtService.class.getDeclaredConstructor(
            String.class, long.class, IRevokedTokenRepository.class, Clock.class);
        constructor.setAccessible(true);
        Clock futureClock = Clock.fixed(Instant.now().plusSeconds(10 * 365 * 24 * 3600L), ZoneOffset.UTC);
        JwtService expiredAwareJwtService = constructor.newInstance(SECRET, 3_600_000L, revokedTokenRepository,
            futureClock);

        assertFalse(expiredAwareJwtService.isTokenValid(token, USER_TESTUSER));
    }

    @Test
    void shouldReturnFalseForWrongUser() {
        String token = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);

        assertFalse(jwtService.isTokenValid(token, USER_OTHER));
    }

    @Test
    void shouldGenerateDistinctTokensForSameUserAndRoleWithinTheSameSecond() {
        String first = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);
        String second = jwtService.generateToken(USER_TESTUSER, CODE_CLIENT);

        assertNotEquals(first, second);
    }
}
