package com.credit.bladi.model.enterprise;

import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import java.time.Instant;
import java.util.UUID;

public record EnterpriseUserLinkModel(
        Long id,
        UUID publicId,
        Long enterpriseId,
        UUID enterprisePublicId,
        Long userId,
        UUID userPublicId,
        EnterpriseLinkRole role,
        EnterpriseLinkStatus status,
        EnterpriseLinkOrigin origin,
        String claimedRegistrationNumber,
        String requestedByUsername,
        String decidedByUsername,
        Instant decidedAt,
        Instant createdAt,
        String username
) {
}
