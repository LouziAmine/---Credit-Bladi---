package com.credit.bladi.config.time;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {
    public static final String BUSINESS_ZONE_ID = "Africa/Casablanca";

    public static final ZoneId BUSINESS_ZONE = ZoneId.of(BUSINESS_ZONE_ID);

    @Bean
    public Clock businessClock() {
        return Clock.system(BUSINESS_ZONE);
    }
}
