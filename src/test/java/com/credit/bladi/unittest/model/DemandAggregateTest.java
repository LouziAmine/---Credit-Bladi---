package com.credit.bladi.unittest.model;

import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_1500000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_2000000;
import static com.credit.bladi.unittest.mock.data.MockAmounts.AMOUNT_6;
import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_5_5;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSIS_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_CLASSIFICATION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DECISION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MISSING_DOCUMENTS;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PROCESS;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ATLAS;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DEM_1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ADMIN;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST2;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_MANAGER1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_SUPERVISOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.exception.WorkflowException.WorkflowExceptionType;
import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.enums.DecisionOutcome;
import com.credit.bladi.model.enums.EnterpriseType;
import com.credit.bladi.model.enums.KeepOrManualOption;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.Demand;
import com.credit.bladi.model.workflow.DemandChange;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.StepClosureDetails;
import com.credit.bladi.model.workflow.StepDecisions;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.unittest.mock.workflow.MockDemand;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DemandAggregateTest {
    private static final Money AMOUNT = Money.of(AMOUNT_1500000);

    private static final Rate RATE = new Rate(AMOUNT_6);

    private static final WorkflowStepInfo ANALYSIS = step(10L, CODE_ANALYSIS_STEP, false);

    private static final WorkflowStepInfo DECISION = step(11L, CODE_DECISION_STEP, false);

    private static final WorkflowStepInfo CLASSIFICATION = step(9L, CODE_CLASSIFICATION_STEP, true);

    private static WorkflowStepInfo step(Long id, String code, boolean requiresClassification) {
        return new WorkflowStepInfo(id, 1L, CODE_PROCESS, "Process", code, code, LABEL_ANALYSTE, id.intValue(), null,
            null,
            null, null, null, null, requiresClassification, false, null, false);
    }

    private static DemandModel openDemand() {
        return demand(null, null);
    }

    private static DemandModel demand(Instant completedAt, Instant rejectedAt) {
        return MockDemand.enterprise(1L, UUID.randomUUID(), LABEL_DEM_1, 1L, LABEL_ATLAS, EnterpriseType.PME, AMOUNT,
            84,
            Instant.now(), completedAt, RATE, null, null, null, null, null, null, null, rejectedAt, null, null);
    }

    private static StepClosureDetails keepTerms(DecisionOutcome outcome, String returnReason) {
        return new StepClosureDetails("note", outcome, returnReason, null, null, null, null, null, null,
            KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null, KeepOrManualOption.KEEP, null);
    }

    private static StepClosureDetails manualTerms() {
        return new StepClosureDetails("note", null, null, null, null, null, null, null, null,
            KeepOrManualOption.MANUAL, DECIMAL_5_5, KeepOrManualOption.MANUAL, AMOUNT_2000000,
            KeepOrManualOption.MANUAL, 60);
    }

    private static WorkflowExceptionType failure(Runnable action) {
        return assertThrows(WorkflowException.class, action::run).getType();
    }

    @Test
    void advancingClosesTheStepAppliesTheDecidedTermsAndOpensTheNextStep() {
        Demand demand = Demand.rehydrate(openDemand(), ANALYSIS, USER_ANALYST1);
        StepClosureDetails details = manualTerms();
        StepDecisions decisions = demand.decide(details, USER_ANALYST1);

        demand.advance(details, decisions, DECISION, USER_MANAGER1, USER_ANALYST1);

        assertEquals(List.of(
            new DemandChange.StepClosed(details, decisions),
            new DemandChange.TermsApplied(new Rate(DECIMAL_5_5), Money.of(AMOUNT_2000000), 60),
            new DemandChange.StepOpened(11L, USER_MANAGER1, USER_ANALYST1)), demand.pendingChanges());
    }

    @Test
    void advancingPastTheLastStepCompletesTheDemand() {
        Demand demand = Demand.rehydrate(openDemand(), DECISION, USER_MANAGER1);
        StepClosureDetails details = keepTerms(null, null);

        demand.advance(details, demand.decide(details, USER_MANAGER1), null, null, USER_MANAGER1);

        assertTrue(demand.pendingChanges().contains(new DemandChange.Completed()));
    }

    @Test
    void keepingTheTermsRecordsThePreviousValuesAsApplied() {
        Demand demand = Demand.rehydrate(openDemand(), ANALYSIS, USER_ANALYST1);

        StepDecisions decisions = demand.decide(keepTerms(null, null), USER_ANALYST1);

        assertEquals(RATE, decisions.rate().appliedRate());
        assertEquals(AMOUNT, decisions.amount().appliedAmount());
        assertEquals(84, decisions.duration().appliedDuration());
        assertTrue(decisions.termChanges().isEmpty());
    }

    @Test
    void refusingClosesTheStepAndRejectsTheDemand() {
        Demand demand = Demand.rehydrate(openDemand(), DECISION, USER_MANAGER1);
        StepClosureDetails details = keepTerms(DecisionOutcome.REJECTED, null);
        StepDecisions decisions = demand.decide(details, USER_MANAGER1);

        demand.refuse(details, decisions);

        assertEquals(List.of(new DemandChange.StepClosed(details, decisions), new DemandChange.Rejected()),
            demand.pendingChanges());
    }

    @Test
    void closedStepCannotBeClosedTwice() {
        Demand demand = Demand.rehydrate(openDemand(), DECISION, USER_MANAGER1);
        StepClosureDetails details = keepTerms(DecisionOutcome.REJECTED, null);
        StepDecisions decisions = demand.decide(details, USER_MANAGER1);
        demand.refuse(details, decisions);

        assertEquals(WorkflowExceptionType.STEP_NOT_FOUND, failure(() -> demand.refuse(details, decisions)));
    }

    @Test
    void finishedDemandsCannotAdvance() {
        Demand completed = Demand.rehydrate(demand(Instant.now(), null), ANALYSIS, null);
        Demand rejected = Demand.rehydrate(demand(null, Instant.now()), ANALYSIS, null);

        assertEquals(WorkflowExceptionType.NO_NEXT_STEP, failure(completed::assertReadyToAdvance));
        assertEquals(WorkflowExceptionType.NO_NEXT_STEP, failure(rejected::assertReadyToAdvance));
    }

    @Test
    void demandWaitingForClassificationMustBeClassifiedBeforeAdvancing() {
        Demand demand = Demand.rehydrate(openDemand(), CLASSIFICATION, null);

        assertEquals(WorkflowExceptionType.MUST_CLASSIFY_FIRST, failure(demand::assertReadyToAdvance));
    }

    @Test
    void onlyADemandAtTheClassificationStepCanBeClassified() {
        Demand demand = Demand.rehydrate(openDemand(), ANALYSIS, null);

        assertEquals(WorkflowExceptionType.NOT_AT_CLASSIFICATION_STEP, failure(demand::assertReadyToClassify));
    }

    @Test
    void demandWithoutAnOpenStepIsReportedAsNotFound() {
        Demand demand = Demand.rehydrate(openDemand(), null, null);

        assertEquals(WorkflowExceptionType.DEMAND_NOT_FOUND, failure(demand::assertReadyToAdvance));
    }

    @Test
    void deferredDecisionMustGoBackRatherThanForward() {
        Demand demand = Demand.rehydrate(openDemand(), DECISION, null);

        assertEquals(WorkflowExceptionType.DECISION_DEFERRED_REQUIRES_RETURN,
            failure(() -> demand.assertDirectionAllowed(keepTerms(DecisionOutcome.DEFERRED, null))));
        demand.assertDirectionAllowed(keepTerms(DecisionOutcome.DEFERRED, CODE_MISSING_DOCUMENTS));
    }

    @Test
    void classifyingAsRealAttachesTheEnterpriseAndOpensTheNextStep() {
        Demand demand = Demand.rehydrate(openDemand(), CLASSIFICATION, null);

        demand.classifyAsReal("ok", USER_ADMIN, ANALYSIS, USER_ANALYST1, 5L);

        assertEquals(List.of(
            new DemandChange.ClassificationClosed(ClassificationOutcome.REAL, "ok", USER_ADMIN),
            new DemandChange.EnterpriseAttached(5L),
            new DemandChange.StepOpened(10L, USER_ANALYST1, USER_ADMIN)), demand.pendingChanges());
    }

    @Test
    void anEnterpriseDemandNeedsAnEnterpriseToBeClassifiedAsReal() {
        Demand demand = Demand.rehydrate(openDemand(), CLASSIFICATION, null);

        assertEquals(WorkflowExceptionType.ENTERPRISE_REQUIRED_FOR_REAL_CLASSIFICATION,
            failure(() -> demand.assertEnterpriseGivenForRealClassification(false)));
    }

    @Test
    void classifyingAsFakeRejectsTheDemand() {
        Demand demand = Demand.rehydrate(openDemand(), CLASSIFICATION, null);

        demand.classifyAsNotReal(ClassificationOutcome.FAKE, "spam", USER_ADMIN);

        assertEquals(List.of(new DemandChange.ClassificationClosed(ClassificationOutcome.FAKE, "spam", USER_ADMIN),
            new DemandChange.Rejected()), demand.pendingChanges());
    }

    @Test
    void reassigningRecordsWhoHadTheStepBefore() {
        Demand demand = Demand.rehydrate(openDemand(), ANALYSIS, USER_ANALYST1);

        String previous = demand.reassign(USER_ANALYST2, USER_SUPERVISOR);

        assertEquals(USER_ANALYST1, previous);
        assertEquals(List.of(new DemandChange.Reassigned(CODE_ANALYSIS_STEP, CODE_ANALYSIS_STEP, USER_ANALYST1,
            USER_ANALYST2,
            USER_SUPERVISOR)), demand.pendingChanges());
    }

    @Test
    void pullingTheChangesHandsThemOverOnce() {
        Demand demand = Demand.rehydrate(openDemand(), ANALYSIS, USER_ANALYST1);
        demand.reassign(USER_ANALYST2, USER_SUPERVISOR);

        assertEquals(1, demand.pullChanges().size());
        assertTrue(demand.pendingChanges().isEmpty());
    }
}
