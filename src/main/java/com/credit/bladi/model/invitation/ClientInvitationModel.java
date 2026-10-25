package com.credit.bladi.model.invitation;

import com.credit.bladi.model.enums.ClientInvitationStatus;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import java.time.Instant;

public record ClientInvitationModel(
        Long id,
        String email,
        String tokenHash,
        Long enterpriseId,
        EnterpriseLinkRole role,
        Long demandId,
        ClientInvitationStatus status,
        String createdByUsername,
        Instant createdAt,
        Instant expiresAt,
        Instant acceptedAt
) {
}
