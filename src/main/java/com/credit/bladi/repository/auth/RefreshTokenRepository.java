package com.credit.bladi.repository.auth;

import com.credit.bladi.datasource.auth.RefreshTokenDatasource;
import com.credit.bladi.datasource.auth.entity.RefreshTokenEntity;
import com.credit.bladi.model.auth.RefreshToken;
import java.time.Clock;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RefreshTokenRepository implements IRefreshTokenRepository {
    private final RefreshTokenDatasource datasource;

    private final Clock clock;

    public RefreshTokenRepository(RefreshTokenDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public void save(String token, Long userId, long expiresAt) {
        datasource.save(new RefreshTokenEntity(TokenHasher.hash(token), userId, expiresAt));
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return datasource.findByTokenHash(TokenHasher.hash(token)).map(entity -> toModel(token, entity));
    }

    @Override
    public void revoke(String token) {
        datasource.revokeByTokenHash(TokenHasher.hash(token));
    }

    @Override
    public boolean revokeIfActive(String token) {
        return datasource.revokeIfActiveByTokenHash(TokenHasher.hash(token)) > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAllForUser(Long userId) {
        datasource.revokeAllByUserId(userId);
    }

    @Override
    public int deleteExpiredTokens() {
        return datasource.deleteExpiredTokens(clock.millis());
    }

    private RefreshToken toModel(String rawToken, RefreshTokenEntity entity) {
        return new RefreshToken(rawToken, entity.getUserId(), entity.getExpiresAt(), entity.isRevoked());
    }
}
