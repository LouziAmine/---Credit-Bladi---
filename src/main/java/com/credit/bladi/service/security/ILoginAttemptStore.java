package com.credit.bladi.service.security;

public interface ILoginAttemptStore {

    boolean isLocked(String username);

    void recordFailure(String username);

    void reset(String username);
}