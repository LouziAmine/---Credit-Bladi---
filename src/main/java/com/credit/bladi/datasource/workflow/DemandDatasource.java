package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.DemandEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandDatasource extends JpaRepository<DemandEntity, Long>,
        JpaSpecificationExecutor<DemandEntity> {
    Optional<DemandEntity> findByPublicId(UUID publicId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM DemandEntity e WHERE e.id = :id")
    Optional<DemandEntity> findByIdForUpdate(@Param("id") Long id);

    @Query(value = "SELECT d.id FROM demand d "
        + "JOIN workflow_process_step_demand wpsd ON wpsd.demand_id = d.id AND wpsd.exited_at IS NULL "
        + "JOIN workflow_process_step wps ON wpsd.workflow_process_step_id = wps.id "
        + "JOIN step s ON wps.step_id = s.id "
        + "WHERE d.demand_type = :demandType AND LOWER(d.contact_email) = LOWER(:contactEmail) "
        + "AND LOWER(TRIM(d.contact_name)) = LOWER(TRIM(:contactName)) "
        + "AND d.completed_at IS NULL AND d.rejected_at IS NULL AND d.deleted_at IS NULL "
        + "AND s.requires_classification = TRUE "
        + "ORDER BY d.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Long> findUnclassifiedDemandId(@Param("demandType") String demandType,
        @Param("contactEmail") String contactEmail, @Param("contactName") String contactName);
}
