package com.credit.bladi.unittest.model;

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
        assertThrows(NullPointerException.class, () -> new RefreshToken("tok", null, 0L, false));
    }

    @Test
    void shouldBeValidWhenNotRevokedAndNotExpired() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken token = new RefreshToken("tok", 1L, futureExpiry, false);

        assertTrue(token.isValid());
        assertFalse(token.isExpired());
    }

    @Test
    void shouldBeInvalidWhenExpired() {
        long pastExpiry = System.currentTimeMillis() - 1L;
        RefreshToken token = new RefreshToken("tok", 1L, pastExpiry, false);

        assertTrue(token.isExpired());
        assertFalse(token.isValid());
    }

    @Test
    void shouldBeInvalidWhenRevoked() {
        long futureExpiry = System.currentTimeMillis() + 60_000L;
        RefreshToken token = new RefreshToken("tok", 1L, futureExpiry, true);

        assertFalse(token.isValid());
    }

    @Test
    void shouldBeInvalidWhenBothRevokedAndExpired() {
        long pastExpiry = System.currentTimeMillis() - 1L;
        RefreshToken token = new RefreshToken("tok", 1L, pastExpiry, true);

        assertFalse(token.isValid());
    }
}