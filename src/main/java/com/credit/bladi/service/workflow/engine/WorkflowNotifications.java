package com.credit.bladi.service.workflow.engine;

import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.user.User;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.model.workflow.WorkflowStepInfo;
import com.credit.bladi.repository.user.IUserRepository;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.notification.DemandContactNotifier;
import com.credit.bladi.service.notification.INotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class WorkflowNotifications {
    private static final Logger LOGGER = LogManager.getLogger(WorkflowNotifications.class);

    private final INotificationService notificationService;

    private final DemandContactNotifier demandContactNotifier;

    private final IUserRepository userRepository;

    private final AfterCommitNotifications afterCommit;

    public WorkflowNotifications(INotificationService notificationService, DemandContactNotifier demandContactNotifier,
            IUserRepository userRepository, AfterCommitNotifications afterCommit) {
        this.notificationService = notificationService;
        this.demandContactNotifier = demandContactNotifier;
        this.userRepository = userRepository;
        this.afterCommit = afterCommit;
    }

    public void assigned(String assigneeUsername, String assignedBy, DemandModel demand, String stepName) {
        if (assigneeUsername != null) {
            userRepository.findByUsername(assigneeUsername)
                .ifPresent(assignee -> assigned(assignee, assignedBy, demand, stepName));
        }
    }

    public void assigned(User assignee, String assignedBy, DemandModel demand, String stepName) {
        toStaff(assignee, NotificationType.DEMAND_ASSIGNED, LocalizedText.of("demandAssigned.title"),
            LocalizedText.of("demandAssigned.body", demand.reference(), assignedBy, stepName), demand);
    }

    public void awaitingAssignment(WorkflowStepInfo step, DemandModel demand) {
        if (step.requiredGradeId() == null || step.requiredTeamId() == null) {
            return;
        }
        LocalizedText title = LocalizedText.of("demandAwaitingAssignment.title");
        LocalizedText body = LocalizedText.of("demandAwaitingAssignment.body", demand.reference(), step.stepName());
        userRepository.findManagersByGradeIdAndTeamId(step.requiredGradeId(), step.requiredTeamId())
            .forEach(user -> toStaff(user, NotificationType.DEMAND_CREATED, title, body, demand));
    }

    public void stepEntered(NotificationType type, DemandModel demand) {
        switch (type) {
            case DEMAND_IN_DECISION -> toContact(demand, demand.enterpriseIdOrNull(), type,
                LocalizedText.of("demandInDecision.title"),
                LocalizedText.of("demandInDecision.body", demand.reference()));
            default -> LOGGER.error("STEP_ENTRY_NOTIFICATION_NOT_WIRED type={} demandId={}", type, demand.id());
        }
    }

    public void enteredPipeline(DemandModel demand, Long enterpriseId) {
        toContact(demand, enterpriseId, NotificationType.DEMAND_ENTERED_PIPELINE,
            LocalizedText.of("demandEnteredPipeline.title"),
            LocalizedText.of("demandEnteredPipeline.body", demand.reference()));
    }

    public void completed(DemandModel demand) {
        toContact(demand, demand.enterpriseIdOrNull(), NotificationType.DEMAND_COMPLETED,
            LocalizedText.of("demandCompleted.title"), LocalizedText.of("demandCompleted.body", demand.reference()));
    }

    public void rejected(DemandModel demand) {
        toContact(demand, demand.enterpriseIdOrNull(), NotificationType.DEMAND_REJECTED,
            LocalizedText.of("demandRejected.title"), LocalizedText.of("demandRejected.body", demand.reference()));
    }

    private void toStaff(User user, NotificationType type, LocalizedText title, LocalizedText body,
            DemandModel demand) {
        NotificationMessage notification = new NotificationMessage(user.getUsername(), user.getEmail(), null,
            user.getPreferredLanguage(), type, title, body, "/admin/demands/" + demand.publicId());
        afterCommit.send(() -> notificationService.notify(notification));
    }

    private void toContact(DemandModel demand, Long enterpriseId, NotificationType type, LocalizedText title,
            LocalizedText body) {
        afterCommit.send(() -> demandContactNotifier.notify(demand, enterpriseId, type, title, body));
    }

}
