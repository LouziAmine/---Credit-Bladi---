package com.credit.bladi.unittest.repository.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.auth.RefreshTokenDatasource;
import com.credit.bladi.datasource.auth.entity.RefreshTokenEntity;
import com.credit.bladi.model.auth.RefreshToken;
import com.credit.bladi.repository.auth.RefreshTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest {

    @Mock
    private RefreshTokenDatasource datasource;

    private RefreshTokenRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RefreshTokenRepository(datasource);
    }

    @Test
    void shouldSaveRefreshToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity("hash", 1L, 9999999L);
        when(datasource.save(any(RefreshTokenEntity.class))).thenReturn(entity);

        repository.save("tok", 1L, 9999999L);

        verify(datasource).save(any(RefreshTokenEntity.class));
    }

    @Test
    void shouldFindRefreshTokenByToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity("hash", 1L, System.currentTimeMillis() + 60000L);
        when(datasource.findByTokenHash(anyString())).thenReturn(Optional.of(entity));

        Optional<RefreshToken> result = repository.findByToken("tok");

        assertTrue(result.isPresent());
        assertEquals("tok", result.get().token());
        assertEquals(1L, result.get().userId());
    }

    @Test
    void shouldReturnEmptyWhenTokenNotFound() {
        when(datasource.findByTokenHash(anyString())).thenReturn(Optional.empty());

        Optional<RefreshToken> result = repository.findByToken("unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldRevokeRefreshToken() {
        repository.revoke("tok");

        verify(datasource).revokeByTokenHash(anyString());
    }

    @Test
    void shouldReturnTrueWhenRevokeIfActiveAffectsARow() {
        when(datasource.revokeIfActiveByTokenHash(anyString())).thenReturn(1);

        assertTrue(repository.revokeIfActive("tok"));
    }

    @Test
    void shouldReturnFalseWhenRevokeIfActiveAffectsNoRow() {
        when(datasource.revokeIfActiveByTokenHash(anyString())).thenReturn(0);

        assertFalse(repository.revokeIfActive("tok"));
    }

    @Test
    void shouldRevokeAllTokensForUser() {
        repository.revokeAllForUser(1L);

        verify(datasource).revokeAllByUserId(1L);
    }

    @Test
    void shouldDeleteExpiredTokens() {
        repository.deleteExpiredTokens();

        verify(datasource).deleteExpiredTokens(anyLong());
    }
}