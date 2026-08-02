package com.credit.bladi.unittest.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.credit.bladi.config.cache.CacheConfig;
import com.credit.bladi.config.scheduler.SchedulerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

class CacheAndSchedulerConfigTest {

    @Test
    void shouldCreateCacheManager() {
        CacheConfig config = new CacheConfig();
        CacheManager manager = config.cacheManager();
        assertNotNull(manager);
    }

    @Test
    void shouldInstantiateSchedulerConfig() {
        SchedulerConfig config = new SchedulerConfig();
        assertNotNull(config);
    }
}