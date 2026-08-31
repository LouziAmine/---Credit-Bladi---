package com.credit.bladi.service.security;

public interface ILoginAttemptStore {
    boolean isLocked(String username, String clientIp);

    void recordFailure(String username, String clientIp);

    void reset(String username, String clientIp);
}
