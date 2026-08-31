package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.DemandDocumentEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandDocumentDatasource extends JpaRepository<DemandDocumentEntity, Long> {
    Optional<DemandDocumentEntity> findByPublicId(UUID publicId);

    @Query("SELECT d.publicId FROM DemandDocumentEntity d WHERE d.id = :id")
    Optional<UUID> findPublicIdById(@Param("id") Long id);

    @Query("SELECT e FROM DemandDocumentEntity e WHERE e.demandId = :demandId ORDER BY e.uploadedAt ASC")
    List<DemandDocumentEntity> findByDemandIdOrderByUploadedAtAsc(@Param("demandId") Long demandId);

    Optional<DemandDocumentEntity> findByIdAndDemandId(Long id, Long demandId);

    Optional<DemandDocumentEntity> findFirstByDemandIdAndDocumentTypeAndStatus(
        Long demandId, String documentType, String status);
}
