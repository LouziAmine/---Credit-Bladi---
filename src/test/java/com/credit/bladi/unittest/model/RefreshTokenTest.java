package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_TOK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.model.auth.RefreshToken;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {
    @Test
    void shouldRejectNullToken() {
        assertThrows(IllegalArgumentException.class, () -> new RefreshToken(null, 1L, 0L, false));
    }

    @Test
    void shouldRejectBlankToken() {
        assertThrows(IllegalArgumentException.class, () -> new RefreshToken("  ", 1L, 0L, false));
    }

    @Test
    void shouldRejectNullUserId() {
        assertThrows(NullPointerException.class, () -> new RefreshToken(TOKEN_TOK, null, 0L, false));
    }

    @Test
    void shouldNotBeExpiredBeforeItsExpiry() {
        RefreshToken token = new RefreshToken(TOKEN_TOK, 1L, 1_000L, false);

        assertFalse(token.isExpiredAt(999L));
        assertFalse(token.isExpiredAt(1_000L));
    }

    @Test
    void shouldBeExpiredAfterItsExpiry() {
        RefreshToken token = new RefreshToken(TOKEN_TOK, 1L, 1_000L, false);

        assertTrue(token.isExpiredAt(1_001L));
    }

    @Test
    void shouldExposeRevokedFlag() {
        assertTrue(new RefreshToken(TOKEN_TOK, 1L, 1_000L, true).revoked());
        assertFalse(new RefreshToken(TOKEN_TOK, 1L, 1_000L, false).revoked());
    }
}
