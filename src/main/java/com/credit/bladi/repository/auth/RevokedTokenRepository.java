package com.credit.bladi.repository.auth;

import com.credit.bladi.datasource.auth.RevokedTokenDatasource;
import com.credit.bladi.datasource.auth.entity.RevokedTokenEntity;
import java.time.Clock;
import org.springframework.stereotype.Repository;

@Repository
public class RevokedTokenRepository implements IRevokedTokenRepository {
    private final RevokedTokenDatasource datasource;

    private final Clock clock;

    public RevokedTokenRepository(RevokedTokenDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public void revoke(String token, long expiresAt) {
        datasource.save(new RevokedTokenEntity(TokenHasher.hash(token), expiresAt));
    }

    @Override
    public int deleteExpiredTokens() {
        return datasource.deleteExpiredTokens(clock.millis());
    }

    @Override
    public boolean isRevoked(String token) {
        return datasource.existsByTokenHash(TokenHasher.hash(token));
    }
}
