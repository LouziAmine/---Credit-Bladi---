package com.credit.bladi.unittest.repository.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

import com.credit.bladi.repository.auth.TokenHasher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class TokenHasherTest {

    @Test
    void shouldHashToken() {
        String hash = TokenHasher.hash("some-refresh-token");

        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void shouldProduceSameHashForSameToken() {
        assertEquals(TokenHasher.hash("tok"), TokenHasher.hash("tok"));
    }

    @Test
    void shouldWrapNoSuchAlgorithmExceptionAsIllegalStateException() {
        try (MockedStatic<MessageDigest> mocked = mockStatic(MessageDigest.class)) {
            mocked.when(() -> MessageDigest.getInstance("SHA-256"))
                .thenThrow(new NoSuchAlgorithmException("simulated"));

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> TokenHasher.hash("tok"));
            assertEquals("SHA-256 algorithm not available", ex.getMessage());
        }
    }
}
