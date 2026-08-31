package com.credit.bladi.exception;

public class WorkflowException extends ApiException {
    private final WorkflowExceptionType type;

    public enum WorkflowExceptionType {
        WORKFLOW_NOT_CONFIGURED,
        DEMAND_NOT_FOUND,
        STEP_NOT_FOUND,
        NO_NEXT_STEP,
        NO_PREVIOUS_STEP,
        ACTOR_NOT_ELIGIBLE_FOR_STEP,
        ACTOR_NOT_ASSIGNED_TO_STEP,
        ASSIGNER_NOT_ELIGIBLE_FOR_STEP,
        ASSIGNEE_NOT_ELIGIBLE_FOR_STEP,
        NEXT_ASSIGNEE_REQUIRED,
        RATE_OPTION_KEEP_UNAVAILABLE,
        MANUAL_RATE_REQUIRED,
        MANUAL_AMOUNT_REQUIRED,
        MANUAL_DURATION_REQUIRED,
        FILE_TYPE_NOT_ALLOWED,
        FILE_TOO_LARGE,
        FILE_INFECTED,
        DOCUMENT_NOT_FOUND,
        DOCUMENT_NOT_READY,
        NOT_AT_CLASSIFICATION_STEP,
        ENTERPRISE_REQUIRED_FOR_REAL_CLASSIFICATION,
        MUST_CLASSIFY_FIRST,
        INSURANCE_ADI_REQUIRED,
        INSURANCE_DAMAGE_REQUIRED,
        DECISION_DEFERRED_REQUIRES_RETURN,
        FOUR_EYES_VIOLATION,
        RATE_ABOVE_LEGAL_MAXIMUM,
        TERMS_FROZEN_AFTER_DECISION
    }

    private WorkflowException(WorkflowExceptionType type, ApiStatus status, String messageKey) {
        super(status, messageKey);
        this.type = type;
    }

    public static WorkflowException of(WorkflowExceptionType type) {
        return switch (type) {
            case WORKFLOW_NOT_CONFIGURED ->
                new WorkflowException(type, ApiStatus.SERVICE_UNAVAILABLE, "workflow.workflowNotConfigured");
            case DEMAND_NOT_FOUND -> new WorkflowException(type, ApiStatus.NOT_FOUND, "workflow.demandNotFound");
            case STEP_NOT_FOUND -> new WorkflowException(type, ApiStatus.NOT_FOUND, "workflow.stepNotFound");
            case NO_NEXT_STEP -> new WorkflowException(type, ApiStatus.CONFLICT, "workflow.noNextStep");
            case NO_PREVIOUS_STEP -> new WorkflowException(type, ApiStatus.CONFLICT, "workflow.noPreviousStep");
            case ACTOR_NOT_ELIGIBLE_FOR_STEP ->
                new WorkflowException(type, ApiStatus.FORBIDDEN, "workflow.actorNotEligibleForStep");
            case ACTOR_NOT_ASSIGNED_TO_STEP ->
                new WorkflowException(type, ApiStatus.FORBIDDEN, "workflow.actorNotAssignedToStep");
            case ASSIGNER_NOT_ELIGIBLE_FOR_STEP ->
                new WorkflowException(type, ApiStatus.FORBIDDEN, "workflow.assignerNotEligibleForStep");
            case ASSIGNEE_NOT_ELIGIBLE_FOR_STEP ->
                new WorkflowException(type, ApiStatus.FORBIDDEN, "workflow.assigneeNotEligibleForStep");
            case NEXT_ASSIGNEE_REQUIRED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.nextAssigneeRequired");
            case MANUAL_RATE_REQUIRED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.manualRateRequired");
            case MANUAL_AMOUNT_REQUIRED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.manualAmountRequired");
            case MANUAL_DURATION_REQUIRED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.manualDurationRequired");
            case RATE_OPTION_KEEP_UNAVAILABLE ->
                new WorkflowException(type, ApiStatus.CONFLICT, "workflow.rateOptionKeepUnavailable");
            case FILE_TYPE_NOT_ALLOWED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.fileTypeNotAllowed");
            case FILE_TOO_LARGE ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.fileTooLarge");
            case FILE_INFECTED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.fileInfected");
            case DOCUMENT_NOT_FOUND -> new WorkflowException(type, ApiStatus.NOT_FOUND, "workflow.documentNotFound");
            case DOCUMENT_NOT_READY -> new WorkflowException(type, ApiStatus.CONFLICT, "workflow.documentNotReady");
            case NOT_AT_CLASSIFICATION_STEP ->
                new WorkflowException(type, ApiStatus.CONFLICT, "workflow.notAtClassificationStep");
            case ENTERPRISE_REQUIRED_FOR_REAL_CLASSIFICATION -> new WorkflowException(type,
                ApiStatus.UNPROCESSABLE_CONTENT, "workflow.enterpriseRequiredForRealClassification");
            case MUST_CLASSIFY_FIRST -> new WorkflowException(type, ApiStatus.CONFLICT, "workflow.mustClassifyFirst");
            case INSURANCE_ADI_REQUIRED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.insuranceAdiRequired");
            case INSURANCE_DAMAGE_REQUIRED ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.insuranceDamageRequired");
            case DECISION_DEFERRED_REQUIRES_RETURN ->
                new WorkflowException(type, ApiStatus.CONFLICT, "workflow.decisionDeferredRequiresReturn");
            case FOUR_EYES_VIOLATION -> new WorkflowException(type, ApiStatus.FORBIDDEN, "workflow.fourEyesViolation");
            case RATE_ABOVE_LEGAL_MAXIMUM ->
                new WorkflowException(type, ApiStatus.UNPROCESSABLE_CONTENT, "workflow.rateAboveLegalMaximum");
            case TERMS_FROZEN_AFTER_DECISION ->
                new WorkflowException(type, ApiStatus.CONFLICT, "workflow.termsFrozenAfterDecision");
        };
    }

    public WorkflowExceptionType getType() {
        return type;
    }
}
