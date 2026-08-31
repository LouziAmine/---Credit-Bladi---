package com.credit.bladi.datasource.enterprise.mapper;

import com.credit.bladi.datasource.enterprise.entity.EnterpriseUserLinkEntity;
import com.credit.bladi.model.enterprise.EnterpriseUserLinkModel;
import com.credit.bladi.model.enums.EnterpriseLinkOrigin;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import java.time.Instant;
import java.util.UUID;

public final class EnterpriseUserLinkEntityMapper {
    private EnterpriseUserLinkEntityMapper() {
    }

    public static EnterpriseUserLinkModel mapFromEntity(EnterpriseUserLinkEntity entity, UUID enterprisePublicId,
            UUID userPublicId, String username) {
        return new EnterpriseUserLinkModel(
            entity.getId(),
            entity.getPublicId(),
            entity.getEnterpriseId(),
            enterprisePublicId,
            entity.getUserId(),
            userPublicId,
            entity.getRole(),
            entity.getStatus(),
            entity.getOrigin(),
            entity.getClaimedRegistrationNumber(),
            entity.getRequestedByUsername(),
            entity.getDecidedByUsername(),
            entity.getDecidedAt(),
            entity.getCreatedAt(),
            username);
    }

    public static EnterpriseUserLinkEntity mapToNewEntity(Long enterpriseId, Long userId, EnterpriseLinkRole role,
            EnterpriseLinkStatus status, EnterpriseLinkOrigin origin, String claimedRegistrationNumber,
            String requestedByUsername, Instant createdAt) {
        EnterpriseUserLinkEntity entity = new EnterpriseUserLinkEntity();
        entity.setEnterpriseId(enterpriseId);
        entity.setUserId(userId);
        entity.setRole(role);
        entity.setStatus(status);
        entity.setOrigin(origin);
        entity.setClaimedRegistrationNumber(claimedRegistrationNumber);
        entity.setRequestedByUsername(requestedByUsername);
        entity.setCreatedAt(createdAt);
        return entity;
    }
}
