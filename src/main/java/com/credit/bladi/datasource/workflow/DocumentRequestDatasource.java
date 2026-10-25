package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.DocumentRequestEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRequestDatasource extends JpaRepository<DocumentRequestEntity, Long> {
    Optional<DocumentRequestEntity> findByPublicId(UUID publicId);

    @Query("SELECT d.publicId FROM DemandEntity d WHERE d.id = :id")
    Optional<UUID> findDemandPublicId(@Param("id") Long id);

    @Query("SELECT d.publicId FROM DemandDocumentEntity d WHERE d.id = :id")
    Optional<UUID> findDocumentPublicId(@Param("id") Long id);

    List<DocumentRequestEntity> findByDemandIdOrderByCreatedAtDesc(Long demandId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE DocumentRequestEntity e SET e.status = com.credit.bladi.model.enums.DocumentRequestStatus."
        + "FULFILLED, e.fulfilledDocumentId = :fulfilledDocumentId, e.fulfilledAt = :fulfilledAt "
        + "WHERE e.id = :id AND e.status = com.credit.bladi.model.enums.DocumentRequestStatus.PENDING")
    int fulfillIfPending(@Param("id") Long id, @Param("fulfilledDocumentId") Long fulfilledDocumentId,
        @Param("fulfilledAt") Instant fulfilledAt);
}
