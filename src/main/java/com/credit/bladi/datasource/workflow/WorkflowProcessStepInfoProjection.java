package com.credit.bladi.datasource.workflow;

public interface WorkflowProcessStepInfoProjection {
    Long getId();

    Long getWorkflowId();

    String getProcessCode();

    String getProcessName();

    String getStepCode();

    String getStepName();

    String getTypicalRole();

    Integer getGlobalOrder();

    Long getRequiredGradeId();

    String getRequiredGradeCode();

    String getRequiredGradeName();

    Long getRequiredTeamId();

    String getRequiredTeamCode();

    String getRequiredTeamName();

    Boolean getRequiresClassification();

    Boolean getRequiresInsuranceConfirmation();

    Boolean getRequiresFourEyes();

    String getNotifyContactOnEnterType();
}
