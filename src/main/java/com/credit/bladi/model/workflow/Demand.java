package com.credit.bladi.model.workflow;

import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DECISION_DEFERRED_REQUIRES_RETURN;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.DEMAND_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.ENTERPRISE_REQUIRED_FOR_REAL_CLASSIFICATION;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.MUST_CLASSIFY_FIRST;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.NOT_AT_CLASSIFICATION_STEP;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.STEP_NOT_FOUND;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.enums.DemandType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Demand {
    private final DemandModel state;

    private final WorkflowStepInfo currentStep;

    private final String currentAssignee;

    private final List<DemandChange> pendingChanges = new ArrayList<>();

    private boolean stepOpen;

    private Demand(DemandModel state, WorkflowStepInfo currentStep, String currentAssignee) {
        this.state = Objects.requireNonNull(state);
        this.currentStep = currentStep;
        this.currentAssignee = currentAssignee;
        this.stepOpen = currentStep != null;
    }

    public static Demand rehydrate(DemandModel state, WorkflowStepInfo currentStep, String currentAssignee) {
        return new Demand(state, currentStep, currentAssignee);
    }

    public Long id() {
        return state.id();
    }

    public DemandModel state() {
        return state;
    }

    public String currentAssignee() {
        return currentAssignee;
    }

    public WorkflowStepInfo currentStep() {
        if (currentStep == null) {
            throw WorkflowException.of(DEMAND_NOT_FOUND);
        }
        return currentStep;
    }

    public void assertOpen() {
        state.assertOpen();
    }

    public void assertReadyToAdvance() {
        assertOpen();
        if (currentStep().requiresClassification()) {
            throw WorkflowException.of(MUST_CLASSIFY_FIRST);
        }
    }

    public void assertReadyToClassify() {
        if (!currentStep().requiresClassification()) {
            throw WorkflowException.of(NOT_AT_CLASSIFICATION_STEP);
        }
    }

    public void assertDirectionAllowed(StepClosureDetails details) {
        if (details.isDeferralMovingForward()) {
            throw WorkflowException.of(DECISION_DEFERRED_REQUIRES_RETURN);
        }
    }

    public void assertEnterpriseGivenForRealClassification(boolean enterpriseGiven) {
        if (isEnterpriseDemand() && !enterpriseGiven) {
            throw WorkflowException.of(ENTERPRISE_REQUIRED_FOR_REAL_CLASSIFICATION);
        }
    }

    public boolean isEnterpriseDemand() {
        return state.demandType() == DemandType.ENTERPRISE;
    }

    public StepDecisions decide(StepClosureDetails details, String actor) {
        return new StepDecisions(details.rateDecision(state.applicableRate(), actor),
            details.amountDecision(state.amount()), details.durationDecision(state.duration()));
    }

    public void refuse(StepClosureDetails details, StepDecisions decisions) {
        closeStep(new DemandChange.StepClosed(details, decisions));
        pendingChanges.add(new DemandChange.Rejected());
    }

    public void advance(StepClosureDetails details, StepDecisions decisions, WorkflowStepInfo destination,
                        String nextAssignee, String actor) {
        closeStep(new DemandChange.StepClosed(details, decisions));
        pendingChanges.add(new DemandChange.TermsApplied(decisions.rate().appliedRate(),
            decisions.amount().appliedAmount(), decisions.duration().appliedDuration()));
        if (destination == null) {
            pendingChanges.add(new DemandChange.Completed());
        } else {
            openStep(destination, nextAssignee, actor);
        }
    }

    public void classifyAsNotReal(ClassificationOutcome outcome, String note, String actor) {
        closeStep(new DemandChange.ClassificationClosed(outcome, note, actor));
        pendingChanges.add(new DemandChange.Rejected());
    }

    public void classifyAsReal(String note, String actor, WorkflowStepInfo destination, String nextAssignee,
                               Long enterpriseId) {
        closeStep(new DemandChange.ClassificationClosed(ClassificationOutcome.REAL, note, actor));
        if (enterpriseId != null) {
            pendingChanges.add(new DemandChange.EnterpriseAttached(enterpriseId));
        }
        openStep(Objects.requireNonNull(destination), nextAssignee, actor);
    }

    public String reassign(String newAssignee, String assignedBy) {
        requireOpenStep();
        pendingChanges.add(new DemandChange.Reassigned(currentStep.stepCode(), currentStep.stepName(),
            currentAssignee, newAssignee, assignedBy));
        return currentAssignee;
    }

    public void refreshFromSimulation(DemandSnapshot snapshot) {
        pendingChanges.add(new DemandChange.SimulationRefreshed(snapshot));
    }

    public List<DemandChange> pendingChanges() {
        return List.copyOf(pendingChanges);
    }

    public List<DemandChange> pullChanges() {
        List<DemandChange> changes = List.copyOf(pendingChanges);
        pendingChanges.clear();
        return changes;
    }

    private void closeStep(DemandChange closure) {
        requireOpenStep();
        pendingChanges.add(closure);
        stepOpen = false;
    }

    private void openStep(WorkflowStepInfo step, String assignee, String assignedBy) {
        pendingChanges.add(new DemandChange.StepOpened(step.workflowProcessStepId(), assignee, assignedBy));
        stepOpen = true;
    }

    private void requireOpenStep() {
        if (!stepOpen) {
            throw WorkflowException.of(STEP_NOT_FOUND);
        }
    }
}
