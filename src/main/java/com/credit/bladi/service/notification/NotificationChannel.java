package com.credit.bladi.service.notification;

import com.credit.bladi.model.notification.RenderedNotification;

public interface NotificationChannel {
    void send(RenderedNotification message);
}
