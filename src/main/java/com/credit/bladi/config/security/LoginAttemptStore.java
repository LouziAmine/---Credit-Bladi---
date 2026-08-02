package com.credit.bladi.config.security;

import com.credit.bladi.service.security.ILoginAttemptStore;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptStore implements ILoginAttemptStore {

    private record AttemptState(int failures, long lockedUntil) {}

    private final int maxAttempts;
    private final long lockoutMs;
    private final Map<String, AttemptState> attempts;

    public LoginAttemptStore(
            @Value("${security.login-lockout.max-attempts:5}") int maxAttempts,
            @Value("${security.login-lockout.duration-ms:900000}") long lockoutMs) {
        this.maxAttempts = maxAttempts;
        this.lockoutMs = lockoutMs;
        this.attempts = Caffeine.newBuilder()
            .maximumSize(50_000)
            .expireAfterWrite(Duration.ofMillis(lockoutMs))
            .<String, AttemptState>build()
            .asMap();
    }

    @Override
    public boolean isLocked(String username) {
        AttemptState state = attempts.get(username);
        return state != null && state.lockedUntil() > System.currentTimeMillis();
    }

    @Override
    public void recordFailure(String username) {
        attempts.compute(username, (key, state) -> {
            int failures = (state == null ? 0 : state.failures()) + 1;
            long lockedUntil = failures >= maxAttempts ? System.currentTimeMillis() + lockoutMs : 0L;
            return new AttemptState(failures, lockedUntil);
        });
    }

    @Override
    public void reset(String username) {
        attempts.remove(username);
    }
}