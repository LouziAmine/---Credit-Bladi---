package com.credit.bladi.datasource.notification;

import com.credit.bladi.datasource.notification.entity.NotificationEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDatasource extends JpaRepository<NotificationEntity, Long> {
    Optional<NotificationEntity> findByPublicId(UUID publicId);

    Page<NotificationEntity> findByUsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    long countByUsernameAndReadFalse(String username);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.read = true WHERE n.username = :username AND n.read = false")
    void markAllAsRead(@Param("username") String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE NotificationEntity n SET n.read = true WHERE n.id = :id AND n.username = :username")
    int markAsReadIfOwnedBy(@Param("id") Long id, @Param("username") String username);
}
