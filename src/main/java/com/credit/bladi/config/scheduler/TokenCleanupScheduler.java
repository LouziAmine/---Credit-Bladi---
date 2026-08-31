package com.credit.bladi.config.scheduler;

import com.credit.bladi.config.security.RateLimitStore;
import com.credit.bladi.config.time.ClockConfig;
import com.credit.bladi.service.auth.ITokenMaintenanceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupScheduler {
    private final ITokenMaintenanceService tokenMaintenanceService;

    private final RateLimitStore rateLimitStore;

    public TokenCleanupScheduler(ITokenMaintenanceService tokenMaintenanceService,
                                 RateLimitStore rateLimitStore) {
        this.tokenMaintenanceService = tokenMaintenanceService;
        this.rateLimitStore = rateLimitStore;
    }

    @Scheduled(cron = "${scheduler.token-cleanup.cron:0 0 3 * * *}", zone = ClockConfig.BUSINESS_ZONE_ID)
    public void purgeExpiredTokens() {
        tokenMaintenanceService.purgeExpiredTokens();
    }

    @Scheduled(fixedDelay = 60_000L)
    public void evictExpiredRateLimitEntries() {
        rateLimitStore.evictExpiredEntries();
    }
}
