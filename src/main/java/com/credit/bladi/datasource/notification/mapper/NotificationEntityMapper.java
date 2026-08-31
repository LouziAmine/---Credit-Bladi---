package com.credit.bladi.datasource.notification.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.notification.entity.NotificationEntity;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.NotificationModel;
import com.credit.bladi.model.notification.RenderedNotification;
import java.time.Instant;
import java.util.List;

public final class NotificationEntityMapper {
    private NotificationEntityMapper() {
    }

    public static NotificationEntity toNewEntity(RenderedNotification message, Instant createdAt) {
        NotificationEntity entity = new NotificationEntity();
        entity.setUsername(message.recipientUsername());
        entity.setType(message.type().name());
        entity.setTitle(message.title());
        entity.setBody(message.body());
        entity.setLink(message.link());
        entity.setRead(false);
        entity.setCreatedAt(createdAt);
        return entity;
    }

    public static NotificationModel mapFromEntity(NotificationEntity entity) {
        return new NotificationModel(entity.getId(), entity.getPublicId(), NotificationType.valueOf(entity.getType()),
            entity.getTitle(), entity.getBody(), entity.getLink(), entity.isRead(), entity.getCreatedAt());
    }

    public static CreditPage<NotificationModel> mapToPage(List<NotificationEntity> entities, long total, int page,
            int size) {
        return CreditPage.of(entities.stream().map(NotificationEntityMapper::mapFromEntity).toList(),
            page, size, total);
    }
}
