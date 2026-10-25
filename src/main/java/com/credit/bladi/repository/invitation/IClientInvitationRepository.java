package com.credit.bladi.repository.invitation;

import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.invitation.ClientInvitationModel;
import java.time.Instant;
import java.util.Optional;

public interface IClientInvitationRepository {
    ClientInvitationModel create(String email, String tokenHash, Long enterpriseId, EnterpriseLinkRole role,
            Long demandId, String createdByUsername, Instant expiresAt);

    Optional<ClientInvitationModel> findByTokenHash(String tokenHash);

    Optional<ClientInvitationModel> findActivePending(String email, Long enterpriseId);

    Optional<ClientInvitationModel> findActivePendingForDemand(String email, Long demandId);

    ClientInvitationModel markAccepted(Long id, Instant acceptedAt);

    int deleteExpired();
}
