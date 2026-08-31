package com.credit.bladi.service.auth;

import com.credit.bladi.repository.auth.IRefreshTokenRepository;
import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import com.credit.bladi.repository.invitation.IClientInvitationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenMaintenanceService implements ITokenMaintenanceService {
    private static final Logger LOGGER = LogManager.getLogger(TokenMaintenanceService.class);

    private final IRevokedTokenRepository revokedTokenRepository;

    private final IRefreshTokenRepository refreshTokenRepository;

    private final IClientInvitationRepository clientInvitationRepository;

    public TokenMaintenanceService(IRevokedTokenRepository revokedTokenRepository,
                                   IRefreshTokenRepository refreshTokenRepository,
                                   IClientInvitationRepository clientInvitationRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.clientInvitationRepository = clientInvitationRepository;
    }

    @Override
    @Transactional
    public void purgeExpiredTokens() {
        int revokedPurged = revokedTokenRepository.deleteExpiredTokens();
        LOGGER.info("Purged {} expired revoked access tokens", revokedPurged);
        int refreshPurged = refreshTokenRepository.deleteExpiredTokens();
        LOGGER.info("Purged {} expired refresh tokens", refreshPurged);
        int invitationsPurged = clientInvitationRepository.deleteExpired();
        LOGGER.info("Purged {} expired client invitations", invitationsPurged);
    }
}
