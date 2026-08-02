package com.credit.bladi.repository.auth;

import com.credit.bladi.datasource.auth.RevokedTokenDatasource;
import com.credit.bladi.datasource.auth.entity.RevokedTokenEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RevokedTokenRepository implements IRevokedTokenRepository {

    private final RevokedTokenDatasource datasource;

    public RevokedTokenRepository(RevokedTokenDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    public void revoke(String token, long expiresAt) {
        datasource.save(new RevokedTokenEntity(TokenHasher.hash(token), expiresAt));
    }

    @Override
    @Transactional
    public int deleteExpiredTokens() {
        return datasource.deleteExpiredTokens(System.currentTimeMillis());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRevoked(String token) {
        return datasource.existsByTokenHash(TokenHasher.hash(token));
    }
}
