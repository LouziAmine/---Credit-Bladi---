package com.credit.bladi.service.servicing;

import com.credit.bladi.model.enums.InstallmentStatus;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.LocalizedText;
import com.credit.bladi.model.servicing.InstallmentModel;
import com.credit.bladi.model.workflow.DemandModel;
import com.credit.bladi.repository.servicing.IInstallmentRepository;
import com.credit.bladi.service.notification.AfterCommitNotifications;
import com.credit.bladi.service.notification.DemandContactNotifier;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OverdueInstallmentProcessor {
    private final IInstallmentRepository installmentRepository;

    private final DemandContactNotifier demandContactNotifier;

    private final AfterCommitNotifications afterCommit;

    public OverdueInstallmentProcessor(IInstallmentRepository installmentRepository,
            DemandContactNotifier demandContactNotifier, AfterCommitNotifications afterCommit) {
        this.installmentRepository = installmentRepository;
        this.demandContactNotifier = demandContactNotifier;
        this.afterCommit = afterCommit;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(InstallmentModel installment, DemandModel demand, Instant now, Instant reminderCutoff) {
        InstallmentModel current = installment.status() == InstallmentStatus.PENDING
            ? installmentRepository.markLate(installment.id())
            : installment;
        if (current.wasRemindedSince(reminderCutoff)) {
            return;
        }
        installmentRepository.recordReminderSent(current.id(), now);
        afterCommit.send(() -> demandContactNotifier.notify(demand, NotificationType.PAYMENT_REMINDER,
            LocalizedText.of("paymentReminder.title"),
            LocalizedText.of("paymentReminder.body", current.sequenceNumber(), current.amount(),
                demand.reference(), current.dueDate())));
    }
}
