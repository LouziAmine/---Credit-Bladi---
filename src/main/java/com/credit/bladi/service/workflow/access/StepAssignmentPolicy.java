package com.credit.bladi.service.workflow.access;

import static com.credit.bladi.exception.AuthException.AuthExceptionType.USER_NOT_FOUND;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.ACTOR_NOT_ASSIGNED_TO_STEP;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.ACTOR_NOT_ELIGIBLE_FOR_STEP;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.ASSIGNEE_NOT_ELIGIBLE_FOR_STEP;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.ASSIGNER_NOT_ELIGIBLE_FOR_STEP;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.FOUR_EYES_VIOLATION;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.NEXT_ASSIGNEE_REQUIRED;
import static com.credit.bladi.exception.WorkflowException.WorkflowExceptionType.RATE_ABOVE_LEGAL_MAXIMUM;

import com.credit.bladi.exception.AuthException;
import com.credit.bladi.exception.WorkflowException;
import com.credit.bladi.model.enums.UserRole;
import com.credit.bladi.model.parameters.RegulatoryLimits;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.workflow.RateDecision;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.repository.workflow.IWorkflowRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StepAssignmentPolicy {
    private final IUserRepository userRepository;

    private final IWorkflowRepository workflowRepository;

    private final RegulatoryLimits regulatoryLimits;

    private final boolean fourEyesEnabled;

    public StepAssignmentPolicy(IUserRepository userRepository, IWorkflowRepository workflowRepository,
            RegulatoryLimits regulatoryLimits, @Value("${workflow.four-eyes.enabled:true}") boolean fourEyesEnabled) {
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.regulatoryLimits = regulatoryLimits;
        this.fourEyesEnabled = fourEyesEnabled;
    }

    public void assertActorEligibleForStep(String username, WorkflowStepInfo step, String assignedUsername) {
        if (assignedUsername != null) {
            if (!assignedUsername.equals(username)) {
                throw WorkflowException.of(ACTOR_NOT_ASSIGNED_TO_STEP);
            }
            return;
        }
        if (step.requiredGradeId() == null || step.requiredTeamId() == null) {
            return;
        }
        User actor = requireUser(username);
        if (actor.hasGradeAndTeam() && !actor.isEligibleFor(step)) {
            throw WorkflowException.of(ACTOR_NOT_ELIGIBLE_FOR_STEP);
        }
    }

    public void assertFourEyes(Long demandId, WorkflowStepInfo step, String username) {
        if (fourEyesEnabled && step.requiresFourEyes() && workflowRepository.hasClosedAnyStep(demandId, username)) {
            throw WorkflowException.of(FOUR_EYES_VIOLATION);
        }
    }

    public void assertWithinLegalRate(RateDecision rateDecision) {
        if (regulatoryLimits.exceedsMaxRate(rateDecision.appliedRate())) {
            throw WorkflowException.of(RATE_ABOVE_LEGAL_MAXIMUM);
        }
    }

    public void assertEligibleNextAssignee(String actorUsername, String assigneeUsername,
            WorkflowStepInfo destination) {
        if (destination.requiredGradeId() == null || destination.requiredTeamId() == null) {
            if (assigneeUsername != null && !assigneeUsername.isBlank()) {
                requireStaff(assigneeUsername);
            }
            return;
        }
        if (assigneeUsername == null || assigneeUsername.isBlank()) {
            throw WorkflowException.of(NEXT_ASSIGNEE_REQUIRED);
        }
        User actor = requireUser(actorUsername);
        User nextAssignee = requireUser(assigneeUsername);
        boolean eligible = actor.isSupervisorOf(destination.requiredTeamId())
            ? nextAssignee.isMemberOf(destination.requiredTeamId())
            : nextAssignee.isEligibleFor(destination);
        if (!eligible) {
            throw WorkflowException.of(ASSIGNEE_NOT_ELIGIBLE_FOR_STEP);
        }
    }

    public User assertCanReassign(Long demandId, String assignerUsername, String targetUsername,
            WorkflowStepInfo current, String currentAssignee) {
        User target = resolveReassignTarget(assignerUsername, targetUsername, current, currentAssignee);
        assertFourEyes(demandId, current, targetUsername);
        return target;
    }

    private User resolveReassignTarget(String assignerUsername, String targetUsername, WorkflowStepInfo current,
            String currentAssignee) {
        if (current.requiredGradeId() == null || current.requiredTeamId() == null) {
            if (currentAssignee != null && !currentAssignee.equals(assignerUsername)) {
                throw WorkflowException.of(ASSIGNER_NOT_ELIGIBLE_FOR_STEP);
            }
            return requireStaff(targetUsername);
        }
        User assigner = requireUser(assignerUsername);
        boolean assignerIsSupervisor = assigner.isSupervisorOf(current.requiredTeamId());
        boolean assignerInTeam = assigner.isMemberOf(current.requiredTeamId());
        if (current.requiredTeamId() != null && !assignerIsSupervisor && !assignerInTeam) {
            throw WorkflowException.of(ASSIGNER_NOT_ELIGIBLE_FOR_STEP);
        }
        User target = requireUser(targetUsername);
        boolean targetEligible = assignerIsSupervisor
            ? target.isMemberOf(current.requiredTeamId())
            : target.isEligibleFor(current);
        if (!targetEligible) {
            throw WorkflowException.of(ASSIGNEE_NOT_ELIGIBLE_FOR_STEP);
        }
        return target;
    }

    public List<User> eligibleAssignees(String actorUsername, WorkflowStepInfo target) {
        if (target == null) {
            return List.of();
        }
        if (target.requiredGradeId() == null || target.requiredTeamId() == null) {
            return userRepository.findAllByRole(UserRole.MANAGER);
        }
        if (requireUser(actorUsername).isSupervisorOf(target.requiredTeamId())) {
            return userRepository.findManagersByTeamId(target.requiredTeamId());
        }
        return userRepository.findManagersByGradeIdAndTeamId(target.requiredGradeId(), target.requiredTeamId());
    }

    private User requireStaff(String username) {
        User user = requireUser(username);
        if (user.getRole() != UserRole.MANAGER) {
            throw WorkflowException.of(ASSIGNEE_NOT_ELIGIBLE_FOR_STEP);
        }
        return user;
    }

    private User requireUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> AuthException.of(USER_NOT_FOUND, username));
    }
}
