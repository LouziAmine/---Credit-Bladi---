package com.credit.bladi.unittest.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.credit.bladi.config.security.CurrentActorProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class CurrentActorProviderTest {

    private final CurrentActorProvider provider = new CurrentActorProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnUsernameWhenAuthenticated() {
        TestingAuthenticationToken auth = new TestingAuthenticationToken("jdoe", "creds");
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertEquals("jdoe", provider.getCurrentUsername());
    }

    @Test
    void shouldReturnNullWhenNoAuthenticationPresent() {
        SecurityContextHolder.clearContext();

        assertNull(provider.getCurrentUsername());
    }

    @Test
    void shouldReturnNullWhenNotAuthenticated() {
        TestingAuthenticationToken auth = new TestingAuthenticationToken("jdoe", "creds");
        auth.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertNull(provider.getCurrentUsername());
    }

    @Test
    void shouldReturnNullForAnonymousUser() {
        TestingAuthenticationToken auth = new TestingAuthenticationToken("anonymousUser", "creds");
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertNull(provider.getCurrentUsername());
    }
}
