package com.credit.bladi.datasource.invitation.mapper;

import com.credit.bladi.datasource.invitation.entity.ClientInvitationEntity;
import com.credit.bladi.model.enums.ClientInvitationStatus;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.invitation.ClientInvitationModel;
import java.time.Instant;

public final class ClientInvitationEntityMapper {
    private ClientInvitationEntityMapper() {
    }

    public static ClientInvitationModel mapFromEntity(ClientInvitationEntity entity) {
        return new ClientInvitationModel(
            entity.getId(),
            entity.getEmail(),
            entity.getTokenHash(),
            entity.getEnterpriseId(),
            entity.getRole(),
            entity.getDemandId(),
            entity.getStatus(),
            entity.getCreatedByUsername(),
            entity.getCreatedAt(),
            entity.getExpiresAt(),
            entity.getAcceptedAt());
    }

    public static ClientInvitationEntity mapToNewEntity(String email, String tokenHash, Long enterpriseId,
            EnterpriseLinkRole role, Long demandId, String createdByUsername, Instant expiresAt, Instant createdAt) {
        ClientInvitationEntity entity = new ClientInvitationEntity();
        entity.setEmail(email);
        entity.setTokenHash(tokenHash);
        entity.setEnterpriseId(enterpriseId);
        entity.setRole(role);
        entity.setDemandId(demandId);
        entity.setStatus(ClientInvitationStatus.PENDING);
        entity.setCreatedByUsername(createdByUsername);
        entity.setCreatedAt(createdAt);
        entity.setExpiresAt(expiresAt);
        return entity;
    }
}
