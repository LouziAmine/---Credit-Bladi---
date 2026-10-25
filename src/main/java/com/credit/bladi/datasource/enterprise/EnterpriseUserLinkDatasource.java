package com.credit.bladi.datasource.enterprise;

import com.credit.bladi.datasource.enterprise.entity.EnterpriseUserLinkEntity;
import com.credit.bladi.model.enums.EnterpriseLinkStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseUserLinkDatasource extends JpaRepository<EnterpriseUserLinkEntity, Long> {
    Optional<EnterpriseUserLinkEntity> findByPublicId(UUID publicId);

    @Query("SELECT e.publicId FROM EnterpriseEntity e WHERE e.id = :id")
    Optional<UUID> findEnterprisePublicId(@Param("id") Long id);

    @Query("SELECT u.publicId FROM UserEntity u WHERE u.id = :id")
    Optional<UUID> findUserPublicId(@Param("id") Long id);

    @Query("SELECT u.username FROM UserEntity u WHERE u.id = :id")
    Optional<String> findUsername(@Param("id") Long id);

    Optional<EnterpriseUserLinkEntity> findByEnterpriseIdAndUserIdAndStatusIn(Long enterpriseId, Long userId,
            List<EnterpriseLinkStatus> statuses);

    Page<EnterpriseUserLinkEntity> findByEnterpriseId(Long enterpriseId, Pageable pageable);

    Page<EnterpriseUserLinkEntity> findByStatus(EnterpriseLinkStatus status, Pageable pageable);

    List<EnterpriseUserLinkEntity> findByUserId(Long userId);

    List<EnterpriseUserLinkEntity> findByUserIdAndStatus(Long userId, EnterpriseLinkStatus status);

    List<EnterpriseUserLinkEntity> findByEnterpriseIdAndStatus(Long enterpriseId, EnterpriseLinkStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EnterpriseUserLinkEntity e SET e.status = :status, e.decidedByUsername = :decidedByUsername, "
        + "e.decidedAt = :decidedAt WHERE e.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") EnterpriseLinkStatus status,
        @Param("decidedByUsername") String decidedByUsername, @Param("decidedAt") Instant decidedAt);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE EnterpriseUserLinkEntity e SET e.status = :status, e.decidedByUsername = :decidedByUsername, "
        + "e.decidedAt = :decidedAt WHERE e.id = :id AND e.status = com.credit.bladi.model.enums."
        + "EnterpriseLinkStatus.PENDING")
    int decideIfPending(@Param("id") Long id, @Param("status") EnterpriseLinkStatus status,
        @Param("decidedByUsername") String decidedByUsername, @Param("decidedAt") Instant decidedAt);
}
