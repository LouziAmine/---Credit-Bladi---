package com.credit.bladi.unittest.repository.auth;

import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_HASH;
import static com.credit.bladi.unittest.mock.data.MockTokens.TOKEN_TOK;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_UNKNOWN;
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
import java.time.Clock;
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
        repository = new RefreshTokenRepository(datasource, Clock.systemUTC());
    }

    @Test
    void shouldSaveRefreshToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity(TOKEN_HASH, 1L, 9999999L);
        when(datasource.save(any(RefreshTokenEntity.class))).thenReturn(entity);

        repository.save(TOKEN_TOK, 1L, 9999999L);

        verify(datasource).save(any(RefreshTokenEntity.class));
    }

    @Test
    void shouldFindRefreshTokenByToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity(TOKEN_HASH, 1L, System.currentTimeMillis() + 60000L);
        when(datasource.findByTokenHash(anyString())).thenReturn(Optional.of(entity));

        Optional<RefreshToken> result = repository.findByToken(TOKEN_TOK);

        assertTrue(result.isPresent());
        assertEquals(TOKEN_TOK, result.get().token());
        assertEquals(1L, result.get().userId());
    }

    @Test
    void shouldReturnEmptyWhenTokenNotFound() {
        when(datasource.findByTokenHash(anyString())).thenReturn(Optional.empty());

        Optional<RefreshToken> result = repository.findByToken(USER_UNKNOWN);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldRevokeRefreshToken() {
        repository.revoke(TOKEN_TOK);

        verify(datasource).revokeByTokenHash(anyString());
    }

    @Test
    void shouldReturnTrueWhenRevokeIfActiveAffectsARow() {
        when(datasource.revokeIfActiveByTokenHash(anyString())).thenReturn(1);

        assertTrue(repository.revokeIfActive(TOKEN_TOK));
    }

    @Test
    void shouldReturnFalseWhenRevokeIfActiveAffectsNoRow() {
        when(datasource.revokeIfActiveByTokenHash(anyString())).thenReturn(0);

        assertFalse(repository.revokeIfActive(TOKEN_TOK));
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
