package com.credit.bladi.service.notification;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.notification.NotificationModel;
import java.util.UUID;

public interface INotificationService {
    void notify(NotificationMessage message);

    CreditPage<NotificationModel> getMyNotifications(int page, int size);

    long getMyUnreadCount();

    Long resolveNotificationId(UUID publicId);

    void markAsRead(Long id);

    void markAllAsRead();
}
