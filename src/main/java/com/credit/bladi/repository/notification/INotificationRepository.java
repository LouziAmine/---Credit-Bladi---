package com.credit.bladi.repository.notification;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.notification.NotificationModel;
import com.credit.bladi.model.notification.RenderedNotification;
import java.util.Optional;
import java.util.UUID;

public interface INotificationRepository {
    void save(RenderedNotification message);

    CreditPage<NotificationModel> findByUsername(String username, int page, int size);

    long countUnread(String username);

    Optional<Long> findIdByPublicId(UUID publicId);

    void markAsRead(Long id, String username);

    void markAllAsRead(String username);
}
