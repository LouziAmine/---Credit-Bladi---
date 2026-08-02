package com.credit.bladi.unittest.repository.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.datasource.auth.RevokedTokenDatasource;
import com.credit.bladi.datasource.auth.entity.RevokedTokenEntity;
import com.credit.bladi.repository.auth.RevokedTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RevokedTokenRepositoryTest {

    @Mock
    private RevokedTokenDatasource datasource;

    private RevokedTokenRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RevokedTokenRepository(datasource);
    }

    @Test
    void shouldRevokeToken() {
        RevokedTokenEntity entity = new RevokedTokenEntity("hash", 9999999L);
        when(datasource.save(any(RevokedTokenEntity.class))).thenReturn(entity);

        repository.revoke("tok", 9999999L);

        verify(datasource).save(any(RevokedTokenEntity.class));
    }

    @Test
    void shouldDeleteExpiredTokens() {
        repository.deleteExpiredTokens();

        verify(datasource).deleteExpiredTokens(anyLong());
    }

    @Test
    void shouldReturnTrueWhenTokenIsRevoked() {
        when(datasource.existsByTokenHash(anyString())).thenReturn(true);

        assertTrue(repository.isRevoked("tok"));
    }

    @Test
    void shouldReturnFalseWhenTokenIsNotRevoked() {
        when(datasource.existsByTokenHash(anyString())).thenReturn(false);

        assertFalse(repository.isRevoked("tok"));
    }
}