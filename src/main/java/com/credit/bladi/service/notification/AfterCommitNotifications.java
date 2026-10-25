package com.credit.bladi.service.notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class AfterCommitNotifications {
    private static final Logger LOGGER = LogManager.getLogger(AfterCommitNotifications.class);

    private final TaskExecutor executor;

    public AfterCommitNotifications(@Qualifier("notificationTaskExecutor") TaskExecutor executor) {
        this.executor = executor;
    }

    public void send(Runnable notification) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            sendSafely(notification);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                sendSafely(notification);
            }
        });
    }

    private void sendSafely(Runnable notification) {
        try {
            executor.execute(() -> runSafely(notification));
        } catch (RuntimeException e) {
            LOGGER.error("AFTER_COMMIT_NOTIFICATION_REJECTED", e);
        }
    }

    private static void runSafely(Runnable notification) {
        try {
            notification.run();
        } catch (RuntimeException e) {
            LOGGER.error("AFTER_COMMIT_NOTIFICATION_FAILED", e);
        }
    }
}
