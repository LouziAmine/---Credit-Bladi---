package com.credit.bladi.datasource.auth;

import com.credit.bladi.datasource.auth.entity.RevokedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RevokedTokenDatasource extends JpaRepository<RevokedTokenEntity, Long> {

    boolean existsByTokenHash(String tokenHash);

    @Transactional
    @Modifying
    @Query("DELETE FROM RevokedTokenEntity r WHERE r.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") long now);
}
