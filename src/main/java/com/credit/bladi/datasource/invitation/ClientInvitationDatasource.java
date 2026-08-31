package com.credit.bladi.datasource.invitation;

import com.credit.bladi.datasource.invitation.entity.ClientInvitationEntity;
import com.credit.bladi.model.enums.ClientInvitationStatus;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientInvitationDatasource extends JpaRepository<ClientInvitationEntity, Long> {
    Optional<ClientInvitationEntity> findByTokenHash(String tokenHash);

    Optional<ClientInvitationEntity> findByEmailAndEnterpriseIdAndStatus(String email, Long enterpriseId,
            ClientInvitationStatus status);

    Optional<ClientInvitationEntity> findByEmailAndDemandIdAndEnterpriseIdIsNullAndStatus(String email, Long demandId,
            ClientInvitationStatus status);

    @Modifying
    @Query("DELETE FROM ClientInvitationEntity c WHERE c.expiresAt < :now")
    int deleteByExpiresAtBefore(@Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ClientInvitationEntity c SET c.status = com.credit.bladi.model.enums.ClientInvitationStatus."
        + "ACCEPTED, c.acceptedAt = :acceptedAt WHERE c.id = :id AND c.status = com.credit.bladi.model.enums."
        + "ClientInvitationStatus.PENDING")
    int markAcceptedIfPending(@Param("id") Long id, @Param("acceptedAt") Instant acceptedAt);
}
