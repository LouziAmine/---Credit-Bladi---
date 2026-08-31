package com.credit.bladi.unittest.service.auth;

import static org.mockito.Mockito.verify;

import com.credit.bladi.repository.auth.IRefreshTokenRepository;
import com.credit.bladi.repository.auth.IRevokedTokenRepository;
import com.credit.bladi.repository.invitation.IClientInvitationRepository;
import com.credit.bladi.service.auth.TokenMaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenMaintenanceServiceTest {
    @Mock
    private IRevokedTokenRepository revokedTokenRepository;

    @Mock
    private IRefreshTokenRepository refreshTokenRepository;

    @Mock
    private IClientInvitationRepository clientInvitationRepository;

    private TokenMaintenanceService service;

    @BeforeEach
    void setUp() {
        service = new TokenMaintenanceService(revokedTokenRepository, refreshTokenRepository,
            clientInvitationRepository);
    }

    @Test
    void shouldPurgeExpiredRevokedAndRefreshTokens() {
        service.purgeExpiredTokens();

        verify(revokedTokenRepository).deleteExpiredTokens();
        verify(refreshTokenRepository).deleteExpiredTokens();
        verify(clientInvitationRepository).deleteExpired();
    }
}
