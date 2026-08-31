package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.WorkflowProcessStepDemandEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowProcessStepDemandDatasource extends JpaRepository<WorkflowProcessStepDemandEntity, Long> {
    String CURRENT_STEP_SUMMARY_SELECT = "SELECT wpsd.demand_id AS demandId, p.name AS processName, "
        + "s.name AS stepName "
        + "FROM workflow_process_step_demand wpsd "
        + "JOIN workflow_process_step wps ON wpsd.workflow_process_step_id = wps.id "
        + "JOIN workflow_process wp ON wps.workflow_process_id = wp.id "
        + "JOIN process p ON wp.process_id = p.id "
        + "JOIN step s ON wps.step_id = s.id ";

    @Query("SELECT e FROM WorkflowProcessStepDemandEntity e WHERE e.demandId = :demandId AND e.exitedAt IS NULL")
    Optional<WorkflowProcessStepDemandEntity> findOpenByDemandId(@Param("demandId") Long demandId);

    boolean existsByDemandIdAndClosedBy(Long demandId, String closedBy);

    @Query("SELECT e FROM WorkflowProcessStepDemandEntity e WHERE e.demandId = :demandId ORDER BY e.enteredAt ASC")
    List<WorkflowProcessStepDemandEntity> findHistoryByDemandId(@Param("demandId") Long demandId);

    @Query(value = CURRENT_STEP_SUMMARY_SELECT + "WHERE wpsd.demand_id IN (:demandIds) AND wpsd.exited_at IS NULL",
        nativeQuery = true)
    List<DemandCurrentStepProjection> findOpenStepSummariesByDemandIds(@Param("demandIds") List<Long> demandIds);
}
