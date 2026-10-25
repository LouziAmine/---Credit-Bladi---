package com.credit.bladi.repository.notification;

import static com.credit.bladi.exception.NotificationException.NotificationExceptionType.NOTIFICATION_NOT_FOUND;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.datasource.notification.NotificationDatasource;
import com.credit.bladi.datasource.notification.entity.NotificationEntity;
import com.credit.bladi.datasource.notification.mapper.NotificationEntityMapper;
import com.credit.bladi.exception.NotificationException;
import com.credit.bladi.model.notification.NotificationModel;
import com.credit.bladi.model.notification.RenderedNotification;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class NotificationRepository implements INotificationRepository {
    private final NotificationDatasource datasource;

    private final Clock clock;

    public NotificationRepository(NotificationDatasource datasource, Clock clock) {
        this.clock = clock;
        this.datasource = datasource;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(RenderedNotification message) {
        datasource.save(NotificationEntityMapper.toNewEntity(message, clock.instant()));
    }

    @Override
    public CreditPage<NotificationModel> findByUsername(String username, int page, int size) {
        Page<NotificationEntity> result = datasource.findByUsernameOrderByCreatedAtDesc(username,
            PageRequest.of(page - 1, size, Sort.by("createdAt").descending()));
        return NotificationEntityMapper.mapToPage(result.getContent(), result.getTotalElements(), page, size);
    }

    @Override
    public long countUnread(String username) {
        return datasource.countByUsernameAndReadFalse(username);
    }

    @Override
    public Optional<Long> findIdByPublicId(UUID publicId) {
        return datasource.findByPublicId(publicId).map(NotificationEntity::getId);
    }

    @Override
    public void markAsRead(Long id, String username) {
        int updated = datasource.markAsReadIfOwnedBy(id, username);
        if (updated == 0) {
            throw NotificationException.of(NOTIFICATION_NOT_FOUND);
        }
    }

    @Override
    public void markAllAsRead(String username) {
        datasource.markAllAsRead(username);
    }
}
