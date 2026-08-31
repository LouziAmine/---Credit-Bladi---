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

    private final int maxAttemptsPerAccount;
    private final int maxAttemptsPerIp;
    private final long lockoutMs;
    private final Map<String, AttemptState> accountAttempts;
    private final Map<String, AttemptState> ipAttempts;

    public LoginAttemptStore(
            @Value("${security.login-lockout.max-attempts:5}") int maxAttemptsPerAccount,
            @Value("${security.login-lockout.max-attempts-per-ip:20}") int maxAttemptsPerIp,
            @Value("${security.login-lockout.duration-ms:900000}") long lockoutMs) {
        this.maxAttemptsPerAccount = maxAttemptsPerAccount;
        this.maxAttemptsPerIp = maxAttemptsPerIp;
        this.lockoutMs = lockoutMs;
        this.accountAttempts = Caffeine.newBuilder()
            .maximumSize(50_000)
            .expireAfterWrite(Duration.ofMillis(lockoutMs))
            .<String, AttemptState>build()
            .asMap();
        this.ipAttempts = Caffeine.newBuilder()
            .maximumSize(50_000)
            .expireAfterWrite(Duration.ofMillis(lockoutMs))
            .<String, AttemptState>build()
            .asMap();
    }

    @Override
    public boolean isLocked(String username, String clientIp) {
        return isLocked(accountAttempts, accountKey(username, clientIp))
            || isLocked(ipAttempts, clientIp);
    }

    private static boolean isLocked(Map<String, AttemptState> store, String key) {
        AttemptState state = store.get(key);
        return state != null && state.lockedUntil() > System.currentTimeMillis();
    }

    @Override
    public void recordFailure(String username, String clientIp) {
        recordFailure(accountAttempts, accountKey(username, clientIp), maxAttemptsPerAccount);
        recordFailure(ipAttempts, clientIp, maxAttemptsPerIp);
    }

    private void recordFailure(Map<String, AttemptState> store, String key, int maxAttempts) {
        store.compute(key, (k, state) -> {
            int failures = (state == null ? 0 : state.failures()) + 1;
            long lockedUntil = failures >= maxAttempts ? System.currentTimeMillis() + lockoutMs : 0L;
            return new AttemptState(failures, lockedUntil);
        });
    }

    @Override
    public void reset(String username, String clientIp) {
        accountAttempts.remove(accountKey(username, clientIp));
    }

    private static String accountKey(String username, String clientIp) {
        return username + '\u0000' + clientIp;
    }
}
