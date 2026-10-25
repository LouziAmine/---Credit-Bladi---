package com.credit.bladi.datasource.workflow;

import com.credit.bladi.datasource.workflow.entity.WorkflowProcessStepEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowProcessStepDatasource extends JpaRepository<WorkflowProcessStepEntity, Long> {
    String INFO_SELECT = "SELECT wps.id AS id, wp.workflow_id AS workflowId, p.code AS processCode, "
        + "p.name AS processName, s.code AS stepCode, s.name AS stepName, s.typical_role AS typicalRole, "
        + "(wp.sequence_order * 1000 + wps.sequence_order) AS globalOrder, "
        + "g.id AS requiredGradeId, g.code AS requiredGradeCode, g.name AS requiredGradeName, "
        + "t.id AS requiredTeamId, t.code AS requiredTeamCode, t.name AS requiredTeamName, "
        + "s.requires_classification AS requiresClassification, "
        + "s.requires_insurance_confirmation AS requiresInsuranceConfirmation, "
        + "s.requires_four_eyes AS requiresFourEyes, "
        + "s.notify_contact_on_enter_type AS notifyContactOnEnterType "
        + "FROM workflow_process_step wps "
        + "JOIN workflow_process wp ON wps.workflow_process_id = wp.id "
        + "JOIN process p ON wp.process_id = p.id "
        + "JOIN step s ON wps.step_id = s.id "
        + "LEFT JOIN grade g ON s.required_grade_id = g.id "
        + "LEFT JOIN team t ON wps.required_team_id = t.id ";

    @Query(value = INFO_SELECT + "WHERE wps.id = :id", nativeQuery = true)
    Optional<WorkflowProcessStepInfoProjection> findInfoById(@Param("id") Long id);

    @Query(value = INFO_SELECT + "WHERE wps.id IN (:ids)", nativeQuery = true)
    List<WorkflowProcessStepInfoProjection> findInfoByIds(@Param("ids") List<Long> ids);

    @Query(value = INFO_SELECT + "WHERE wp.workflow_id = :workflowId ORDER BY globalOrder ASC LIMIT 1",
        nativeQuery = true)
    Optional<WorkflowProcessStepInfoProjection> findFirstStep(@Param("workflowId") Long workflowId);

    @Query(value = INFO_SELECT
        + "WHERE wp.workflow_id = :workflowId AND (wp.sequence_order * 1000 + wps.sequence_order) > :afterOrder "
        + "ORDER BY globalOrder ASC LIMIT 1", nativeQuery = true)
    Optional<WorkflowProcessStepInfoProjection> findNextStep(
        @Param("workflowId") Long workflowId, @Param("afterOrder") Integer afterOrder);

    @Query(value = INFO_SELECT
        + "WHERE wp.workflow_id = :workflowId AND (wp.sequence_order * 1000 + wps.sequence_order) < :beforeOrder "
        + "ORDER BY globalOrder DESC LIMIT 1", nativeQuery = true)
    Optional<WorkflowProcessStepInfoProjection> findPreviousStep(
        @Param("workflowId") Long workflowId, @Param("beforeOrder") Integer beforeOrder);

    @Query(value = INFO_SELECT + "WHERE wp.workflow_id = :workflowId ORDER BY globalOrder ASC", nativeQuery = true)
    List<WorkflowProcessStepInfoProjection> findAllSteps(@Param("workflowId") Long workflowId);
}
