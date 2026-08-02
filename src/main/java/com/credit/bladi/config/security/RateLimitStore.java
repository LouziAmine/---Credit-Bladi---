package com.credit.bladi.config.security;

import com.github.benmanes.caffeine.cache.Caffeine;
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

    public boolean isRateLimited(String key, int maxRequests) {
        AtomicBoolean limited = new AtomicBoolean(false);
        timestamps.compute(key, (k, deque) -> {
            if (deque == null) {
                deque = new ArrayDeque<>();
            }
            long now = System.currentTimeMillis();
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
        long cutoff = System.currentTimeMillis() - WINDOW_MS;
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