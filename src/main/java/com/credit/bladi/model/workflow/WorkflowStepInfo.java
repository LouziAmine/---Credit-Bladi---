package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.NotificationType;

public record WorkflowStepInfo(
        Long workflowProcessStepId,
        Long workflowId,
        String processCode,
        String processName,
        String stepCode,
        String stepName,
        String typicalRole,
        Integer globalOrder,
        Long requiredGradeId,
        String requiredGradeCode,
        String requiredGradeName,
        Long requiredTeamId,
        String requiredTeamCode,
        String requiredTeamName,
        boolean requiresClassification,
        boolean requiresInsuranceConfirmation,
        NotificationType notifyContactOnEnterType,
        boolean requiresFourEyes
) {
    public WorkflowStepInfo(Long workflowProcessStepId, Long workflowId, String processCode, String processName,
            String stepCode, String stepName, String typicalRole, Integer globalOrder, Long requiredGradeId,
            String requiredGradeCode, String requiredGradeName, Long requiredTeamId, String requiredTeamCode,
            String requiredTeamName) {
        this(workflowProcessStepId, workflowId, processCode, processName, stepCode, stepName, typicalRole,
            globalOrder, requiredGradeId, requiredGradeCode, requiredGradeName, requiredTeamId, requiredTeamCode,
            requiredTeamName, false, false, null, false);
    }
}
