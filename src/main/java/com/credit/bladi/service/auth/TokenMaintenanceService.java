package com.credit.bladi.service.auth;

import com.credit.bladi.repository.auth.IRefreshTokenRepository;
import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class TokenMaintenanceService implements ITokenMaintenanceService {

    private static final Logger LOGGER = LogManager.getLogger(TokenMaintenanceService.class);

    private final IRevokedTokenRepository revokedTokenRepository;

    private final IRefreshTokenRepository refreshTokenRepository;

    public TokenMaintenanceService(IRevokedTokenRepository revokedTokenRepository,
                                   IRefreshTokenRepository refreshTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void purgeExpiredTokens() {
        int revokedPurged = revokedTokenRepository.deleteExpiredTokens();
        LOGGER.info("Purged {} expired revoked access tokens", revokedPurged);
        int refreshPurged = refreshTokenRepository.deleteExpiredTokens();
        LOGGER.info("Purged {} expired refresh tokens", refreshPurged);
    }
}
