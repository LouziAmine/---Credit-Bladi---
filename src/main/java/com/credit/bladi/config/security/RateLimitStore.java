package com.credit.bladi.config.security;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Component;

@Component
public class RateLimitStore {
    static final long WINDOW_MS = 60_000L;

    private final Map<String, Deque<Long>> timestamps = Caffeine.newBuilder()
        .maximumSize(50_000)
        .<String, Deque<Long>>build()
        .asMap();

    private final Clock clock;

    public RateLimitStore(Clock clock) {
        this.clock = clock;
    }

    public boolean isRateLimited(String key, int maxRequests) {
        AtomicBoolean limited = new AtomicBoolean(false);
        timestamps.compute(key, (k, deque) -> {
            if (deque == null) {
                deque = new ArrayDeque<>();
            }
            long now = clock.millis();
            while (!deque.isEmpty() && deque.peekFirst() < now - WINDOW_MS) {
                deque.pollFirst();
            }
            if (deque.size() >= maxRequests) {
                limited.set(true);
            } else {
                deque.addLast(now);
            }
            return deque;
        });
        return limited.get();
    }

    public void evictExpiredEntries() {
        long cutoff = clock.millis() - WINDOW_MS;
        timestamps.forEach((key, ignored) -> timestamps.compute(key, (k, deque) -> {
            if (deque == null) {
                return null;
            }
            while (!deque.isEmpty() && deque.peekFirst() < cutoff) {
                deque.pollFirst();
            }
            return deque.isEmpty() ? null : deque;
        }));
    }
}
