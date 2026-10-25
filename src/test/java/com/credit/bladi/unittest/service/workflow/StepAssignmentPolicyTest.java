package com.credit.bladi.unittest.service.workflow;

import static com.credit.bladi.unittest.mock.data.MockAmounts.DECIMAL_14_00;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MORTGAGE_DECISION;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_MORTGAGE_DECISION_STEP;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_PAE;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS;
import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_RISK_ANALYSIS_STEP;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSE_CONTRE_RISQUE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_ANALYSTE_PAE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECISION_2;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DECISION_DU_DIRECTEUR_D_AGENCE;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DIRECTEUR_D_AGENCE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_ANALYST1;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_CHARGEE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_DECIDER;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_INSTRUCTOR;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_INTRUDER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.exception.WorkflowException.WorkflowExceptionType;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.vo.Rate;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.repository.workflow.IWorkflowRepository;
import com.credit.bladi.service.workflow.access.StepAssignmentPolicy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StepAssignmentPolicyTest {
    @Mock
    private IUserRepository userRepository;

    @Mock
    private IWorkflowRepository workflowRepository;

    private StepAssignmentPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new StepAssignmentPolicy(userRepository, workflowRepository,
            new RegulatoryLimits(Rate.of(DECIMAL_14_00)), true);
    }

    private static User staff(long id, String username) {
        return User.getBuilder().id(id).publicId(UUID.randomUUID()).username(username).role(UserRole.MANAGER).build();
    }

    private static WorkflowStepInfo profileLessStep() {
        return new WorkflowStepInfo(18L, 3L, CODE_MORTGAGE_DECISION, LABEL_DECISION_2, CODE_MORTGAGE_DECISION_STEP,
            LABEL_DECISION_DU_DIRECTEUR_D_AGENCE, LABEL_DIRECTEUR_D_AGENCE, 3, null, null, null, null, null, null);
    }

    private static WorkflowStepInfo profileLessFourEyesStep() {
        return new WorkflowStepInfo(18L, 3L, CODE_MORTGAGE_DECISION, LABEL_DECISION_2, CODE_MORTGAGE_DECISION_STEP,
            LABEL_DECISION_DU_DIRECTEUR_D_AGENCE, LABEL_DIRECTEUR_D_AGENCE, 3, null, null, null, null, null, null,
            false, false, null, true);
    }

    private static WorkflowStepInfo analysisStep() {
        return new WorkflowStepInfo(6L, 1L, CODE_RISK_ANALYSIS, LABEL_ANALYSE_CONTRE_RISQUE, CODE_RISK_ANALYSIS_STEP,
            LABEL_CONTRE_ANALYSE_DU_DOSSIER_DE_CREDIT, LABEL_ANALYSTE_PAE, 3002, 3L, CODE_ANALYSTE, LABEL_ANALYSTE, 5L,
            CODE_PAE, CODE_PAE);
    }

    @Test
    void shouldOfferEveryStaffMemberForAStepWithoutRequiredProfile() {
        List<User> allStaff = List.of(staff(1L, USER_INSTRUCTOR), staff(2L, USER_DECIDER));
        when(userRepository.findAllByRole(UserRole.MANAGER)).thenReturn(allStaff);

        assertEquals(allStaff, policy.eligibleAssignees(USER_INSTRUCTOR, profileLessStep()));
    }

    @Test
    void shouldOfferOnlyTheRequiredGradeAndTeamOtherwise() {
        User actor = staff(1L, USER_CHARGEE);
        List<User> analysts = List.of(staff(7L, USER_ANALYST1));
        when(userRepository.findByUsername(USER_CHARGEE)).thenReturn(Optional.of(actor));
        when(userRepository.findManagersByGradeIdAndTeamId(3L, 5L)).thenReturn(analysts);

        assertEquals(analysts, policy.eligibleAssignees(USER_CHARGEE, analysisStep()));
    }

    @Test
    void shouldOfferNobodyAfterTheLastStep() {
        assertTrue(policy.eligibleAssignees("anyone", null).isEmpty());
    }

    @Test
    void shouldAcceptAnyStaffMemberAsNextAssigneeOfAStepWithoutRequiredProfile() {
        when(userRepository.findByUsername(USER_DECIDER)).thenReturn(Optional.of(staff(2L, USER_DECIDER)));

        assertDoesNotThrow(() -> policy.assertEligibleNextAssignee(USER_INSTRUCTOR, USER_DECIDER, profileLessStep()));
    }

    @Test
    void shouldRejectNonAssigneeOnStepWithoutRequiredProfile() {
        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> policy.assertActorEligibleForStep(USER_INTRUDER, profileLessStep(), USER_INSTRUCTOR));

        assertEquals(WorkflowExceptionType.ACTOR_NOT_ASSIGNED_TO_STEP, ex.getType());
    }

    @Test
    void shouldLetTheAssigneeActOnStepWithoutRequiredProfile() {
        assertDoesNotThrow(() -> policy.assertActorEligibleForStep(USER_INSTRUCTOR, profileLessStep(),
            USER_INSTRUCTOR));
    }

    @Test
    void shouldRejectReassignByNonAssigneeOnStepWithoutRequiredProfile() {
        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> policy.assertCanReassign(1L, USER_INTRUDER, USER_INTRUDER, profileLessStep(), USER_INSTRUCTOR));

        assertEquals(WorkflowExceptionType.ASSIGNER_NOT_ELIGIBLE_FOR_STEP, ex.getType());
    }

    @Test
    void shouldLetTheAssigneeHandOverStepWithoutRequiredProfile() {
        when(userRepository.findByUsername(USER_DECIDER)).thenReturn(Optional.of(staff(2L, USER_DECIDER)));

        assertEquals(USER_DECIDER,
            policy.assertCanReassign(1L, USER_INSTRUCTOR, USER_DECIDER, profileLessStep(),
            USER_INSTRUCTOR).getUsername());
    }

    @Test
    void shouldRejectReassigningFourEyesStepToSomeoneWhoAlreadyClosedAStep() {
        when(userRepository.findByUsername(USER_INSTRUCTOR)).thenReturn(Optional.of(staff(1L, USER_INSTRUCTOR)));
        when(workflowRepository.hasClosedAnyStep(1L, USER_INSTRUCTOR)).thenReturn(true);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> policy.assertCanReassign(1L, USER_DECIDER, USER_INSTRUCTOR, profileLessFourEyesStep(), USER_DECIDER));

        assertEquals(WorkflowExceptionType.FOUR_EYES_VIOLATION, ex.getType());
    }

    @Test
    void shouldRejectDecisionByActorWhoClosedAnEarlierStep() {
        when(workflowRepository.hasClosedAnyStep(1L, USER_INSTRUCTOR)).thenReturn(true);

        WorkflowException ex = assertThrows(WorkflowException.class,
            () -> policy.assertFourEyes(1L, profileLessFourEyesStep(), USER_INSTRUCTOR));

        assertEquals(WorkflowExceptionType.FOUR_EYES_VIOLATION, ex.getType());
    }
}
