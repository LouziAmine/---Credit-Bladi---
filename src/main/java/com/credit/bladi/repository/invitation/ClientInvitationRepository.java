package com.credit.bladi.repository.invitation;

import com.credit.bladi.datasource.invitation.ClientInvitationDatasource;
import com.credit.bladi.datasource.invitation.entity.ClientInvitationEntity;
import com.credit.bladi.datasource.invitation.mapper.ClientInvitationEntityMapper;
import com.credit.bladi.model.enums.ClientInvitationStatus;
import com.credit.bladi.model.enums.EnterpriseLinkRole;
import com.credit.bladi.model.invitation.ClientInvitationModel;
import com.credit.bladi.repository.DuplicateEntryException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

@Repository
public class ClientInvitationRepository implements IClientInvitationRepository {
    private final ClientInvitationDatasource datasource;

    private final Clock clock;

    public ClientInvitationRepository(ClientInvitationDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    public ClientInvitationModel create(String email, String tokenHash, Long enterpriseId, EnterpriseLinkRole role,
            Long demandId, String createdByUsername, Instant expiresAt) {
        ClientInvitationEntity entity = ClientInvitationEntityMapper.mapToNewEntity(email, tokenHash, enterpriseId,
            role, demandId, createdByUsername, expiresAt, clock.instant());
        try {
            datasource.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntryException(e);
        }
        return ClientInvitationEntityMapper.mapFromEntity(entity);
    }

    @Override
    public Optional<ClientInvitationModel> findByTokenHash(String tokenHash) {
        return datasource.findByTokenHash(tokenHash).map(ClientInvitationEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<ClientInvitationModel> findActivePending(String email, Long enterpriseId) {
        return datasource.findByEmailAndEnterpriseIdAndStatus(email, enterpriseId, ClientInvitationStatus.PENDING)
            .map(ClientInvitationEntityMapper::mapFromEntity);
    }

    @Override
    public Optional<ClientInvitationModel> findActivePendingForDemand(String email, Long demandId) {
        return datasource.findByEmailAndDemandIdAndEnterpriseIdIsNullAndStatus(email, demandId,
                ClientInvitationStatus.PENDING)
            .map(ClientInvitationEntityMapper::mapFromEntity);
    }

    @Override
    public ClientInvitationModel markAccepted(Long id, Instant acceptedAt) {
        int updated = datasource.markAcceptedIfPending(id, acceptedAt);
        if (updated == 0) {
            throw new OptimisticLockingFailureException("ClientInvitation " + id + " is no longer PENDING");
        }
        return datasource.findById(id).map(ClientInvitationEntityMapper::mapFromEntity).orElseThrow();
    }

    @Override
    public int deleteExpired() {
        return datasource.deleteByExpiresAtBefore(clock.instant());
    }
}
