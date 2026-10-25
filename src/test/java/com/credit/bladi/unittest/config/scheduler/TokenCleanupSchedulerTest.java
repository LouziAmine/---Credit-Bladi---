package com.credit.bladi.unittest.config.scheduler;

import static org.mockito.Mockito.verify;

import com.credit.bladi.config.scheduler.TokenCleanupScheduler;
import com.credit.bladi.config.security.RateLimitStore;
import com.credit.bladi.service.auth.ITokenMaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenCleanupSchedulerTest {
    @Mock
    private ITokenMaintenanceService tokenMaintenanceService;

    @Mock
    private RateLimitStore rateLimitStore;

    private TokenCleanupScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new TokenCleanupScheduler(tokenMaintenanceService, rateLimitStore);
    }

    @Test
    void shouldDelegatePurgeExpiredTokensToTokenMaintenanceService() {
        scheduler.purgeExpiredTokens();

        verify(tokenMaintenanceService).purgeExpiredTokens();
    }

    @Test
    void shouldEvictExpiredRateLimitEntries() {
        scheduler.evictExpiredRateLimitEntries();

        verify(rateLimitStore).evictExpiredEntries();
    }
}
