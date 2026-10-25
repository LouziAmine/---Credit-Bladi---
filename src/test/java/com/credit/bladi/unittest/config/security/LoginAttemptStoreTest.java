package com.credit.bladi.unittest.config.security;

import static com.credit.bladi.unittest.mock.data.MockContacts.IP_203_0_113_1;
import static com.credit.bladi.unittest.mock.data.MockContacts.IP_203_0_113_2;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_BRAND_NEW_USERNAME;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_OTHER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.config.security.LoginAttemptStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginAttemptStoreTest {
    private static final String IP_A = IP_203_0_113_1;
    private static final String IP_B = IP_203_0_113_2;

    private LoginAttemptStore store;

    @BeforeEach
    void setUp() {
        store = new LoginAttemptStore(3, 20, 900_000L);
    }

    @Test
    void shouldNotBeLockedInitially() {
        assertFalse(store.isLocked(USER_JDOE, IP_A));
    }

    @Test
    void shouldNotLockBelowMaxAttempts() {
        store.recordFailure(USER_JDOE, IP_A);

        assertFalse(store.isLocked(USER_JDOE, IP_A));
    }

    @Test
    void shouldLockOnceMaxAttemptsReachedFromSameIp() {
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);

        assertTrue(store.isLocked(USER_JDOE, IP_A));
    }

    @Test
    void shouldStayLockedOnSubsequentChecks() {
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);

        assertTrue(store.isLocked(USER_JDOE, IP_A));
        assertTrue(store.isLocked(USER_JDOE, IP_A));
    }

    @Test
    void shouldTrackDifferentUsernamesIndependently() {
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);

        assertTrue(store.isLocked(USER_JDOE, IP_A));
        assertFalse(store.isLocked(USER_OTHER, IP_A));
    }

    @Test
    void shouldUnlockAfterReset() {
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);
        assertTrue(store.isLocked(USER_JDOE, IP_A));

        store.reset(USER_JDOE, IP_A);

        assertFalse(store.isLocked(USER_JDOE, IP_A));
    }

    @Test
    void shouldNotLockOnRepeatedLockChecksSinceCheckingHasNoSideEffect() {
        for (int i = 0; i < 10; i++) {
            assertFalse(store.isLocked(USER_JDOE, IP_A));
        }
    }

    @Test
    void shouldNotLockVictimAccountWhenAttackerFailsFromADifferentIp() {
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);
        store.recordFailure(USER_JDOE, IP_A);

        assertTrue(store.isLocked(USER_JDOE, IP_A));
        assertFalse(store.isLocked(USER_JDOE, IP_B));
    }

    @Test
    void shouldLockAnAbusiveIpOutrightOnceItExceedsThePerIpCeilingAcrossUsernames() {
        for (int i = 0; i < 19; i++) {
            store.recordFailure("user-" + i, IP_A);
        }
        assertFalse(store.isLocked(USER_BRAND_NEW_USERNAME, IP_A));

        store.recordFailure("user-19", IP_A);

        assertTrue(store.isLocked(USER_BRAND_NEW_USERNAME, IP_A));
        assertFalse(store.isLocked(USER_BRAND_NEW_USERNAME, IP_B));
    }
}
