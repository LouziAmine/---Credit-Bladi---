package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.ClassificationOutcome;
import com.credit.bladi.model.vo.Money;
import com.credit.bladi.model.vo.Rate;

public sealed interface DemandChange {
    record StepClosed(StepClosureDetails details, StepDecisions decisions) implements DemandChange {
    }

    record ClassificationClosed(ClassificationOutcome outcome, String note, String closedBy) implements DemandChange {
    }

    record TermsApplied(Rate rate, Money amount, Integer duration) implements DemandChange {
    }

    record StepOpened(Long workflowProcessStepId, String assignee, String assignedBy) implements DemandChange {
    }

    record EnterpriseAttached(Long enterpriseId) implements DemandChange {
    }

    record Reassigned(String stepCode, String stepName, String previousUsername, String newUsername,
                      String assignedBy) implements DemandChange {
    }

    record SimulationRefreshed(DemandSnapshot snapshot) implements DemandChange {
    }

    record Completed() implements DemandChange {
    }

    record Rejected() implements DemandChange {
    }
}
