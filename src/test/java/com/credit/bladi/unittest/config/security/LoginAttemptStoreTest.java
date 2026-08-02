package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.config.security.LoginAttemptStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginAttemptStoreTest {

    private LoginAttemptStore store;

    @BeforeEach
    void setUp() {
        store = new LoginAttemptStore(3, 900_000L);
    }

    @Test
    void shouldNotBeLockedInitially() {
        assertFalse(store.isLocked("jdoe"));
    }

    @Test
    void shouldNotLockBelowMaxAttempts() {
        store.recordFailure("jdoe");

        assertFalse(store.isLocked("jdoe"));
    }

    @Test
    void shouldLockOnceMaxAttemptsReached() {
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");

        assertTrue(store.isLocked("jdoe"));
    }

    @Test
    void shouldStayLockedOnSubsequentChecks() {
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");

        assertTrue(store.isLocked("jdoe"));
        assertTrue(store.isLocked("jdoe"));
    }

    @Test
    void shouldTrackDifferentUsernamesIndependently() {
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");

        assertTrue(store.isLocked("jdoe"));
        assertFalse(store.isLocked("other"));
    }

    @Test
    void shouldUnlockAfterReset() {
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");
        store.recordFailure("jdoe");
        assertTrue(store.isLocked("jdoe"));

        store.reset("jdoe");

        assertFalse(store.isLocked("jdoe"));
    }

    @Test
    void shouldNotLockOnRepeatedLockChecksSinceCheckingHasNoSideEffect() {
        for (int i = 0; i < 10; i++) {
            assertFalse(store.isLocked("jdoe"));
        }
    }
}
