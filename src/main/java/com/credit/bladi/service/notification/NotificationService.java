package com.credit.bladi.service.notification;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.exception.NotificationException;
import com.credit.bladi.model.notification.NotificationMessage;
import com.credit.bladi.model.notification.NotificationModel;
import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.repository.notification.INotificationRepository;
import com.credit.bladi.service.security.ICurrentActorProvider;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService implements INotificationService {
    private final List<NotificationChannel> channels;

    private final INotificationRepository notificationRepository;

    private final ICurrentActorProvider currentActorProvider;

    private final NotificationTexts texts;

    public NotificationService(List<NotificationChannel> channels, INotificationRepository notificationRepository,
            ICurrentActorProvider currentActorProvider, NotificationTexts texts) {
        this.channels = channels;
        this.notificationRepository = notificationRepository;
        this.currentActorProvider = currentActorProvider;
        this.texts = texts;
    }

    @Override
    public void notify(NotificationMessage message) {
        RenderedNotification rendered = new RenderedNotification(message.recipientUsername(),
            message.recipientEmail(), message.recipientPhone(), message.type(),
            texts.render(message.title(), message.recipientLanguage()),
            texts.render(message.body(), message.recipientLanguage()), message.link());
        channels.forEach(channel -> channel.send(rendered));
    }

    @Override
    @Transactional(readOnly = true)
    public CreditPage<NotificationModel> getMyNotifications(int page, int size) {
        return notificationRepository.findByUsername(currentActorProvider.getCurrentUsername(), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public long getMyUnreadCount() {
        return notificationRepository.countUnread(currentActorProvider.getCurrentUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveNotificationId(UUID publicId) {
        return notificationRepository.findIdByPublicId(publicId)
            .orElseThrow(() -> NotificationException.of(NotificationException.NotificationExceptionType
                .NOTIFICATION_NOT_FOUND));
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.markAsRead(id, currentActorProvider.getCurrentUsername());
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        notificationRepository.markAllAsRead(currentActorProvider.getCurrentUsername());
    }
}
