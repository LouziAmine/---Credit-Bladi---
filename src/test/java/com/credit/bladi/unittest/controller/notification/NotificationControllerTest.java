package com.credit.bladi.unittest.controller.notification;

import static com.credit.bladi.unittest.mock.data.MockCodes.CODE_DEMAND_ASSIGNED;
import static com.credit.bladi.unittest.mock.data.MockLabels.LABEL_DOSSIER_AFFECTE;
import static com.credit.bladi.unittest.mock.data.MockUsers.USER_JDOE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.notification.response.NotificationResponseDTO;
import com.credit.bladi.controller.notification.NotificationController;
import com.credit.bladi.infrastructure.notification.SseEmitterRegistry;
import com.credit.bladi.model.enums.NotificationType;
import com.credit.bladi.model.notification.NotificationModel;
import com.credit.bladi.service.notification.INotificationService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
    @Mock
    private INotificationService notificationService;

    @Mock
    private SseEmitterRegistry sseEmitterRegistry;

    @Mock
    private UserDetails currentUser;

    private NotificationController controller;

    @BeforeEach
    void setUp() {
        controller = new NotificationController(notificationService, sseEmitterRegistry);
    }

    @Test
    void shouldReturn200WithMappedNotificationsPage() {
        UUID publicId = UUID.randomUUID();
        CreditPage<NotificationModel> page = new CreditPage<>();
        page.setItems(List.of(new NotificationModel(1L, publicId, NotificationType.DEMAND_ASSIGNED,
            LABEL_DOSSIER_AFFECTE, "body", "/portal", false, Instant.now())));
        page.setCurrentPage(1);
        page.setLastPage(1);
        page.setSize(20);
        page.setTotal(1);
        when(notificationService.getMyNotifications(1, 20)).thenReturn(page);

        ResponseEntity<CreditPage<NotificationResponseDTO>> result = controller.getMyNotifications(1, 20);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getItems().size());
        assertEquals(publicId, result.getBody().getItems().get(0).id());
        assertEquals(CODE_DEMAND_ASSIGNED, result.getBody().getItems().get(0).type());
    }

    @Test
    void shouldDelegateGetMyNotificationsWithGivenPageAndSize() {
        when(notificationService.getMyNotifications(2, 10)).thenReturn(new CreditPage<>());

        controller.getMyNotifications(2, 10);

        verify(notificationService).getMyNotifications(2, 10);
    }

    @Test
    void shouldReturn200WithUnreadCount() {
        when(notificationService.getMyUnreadCount()).thenReturn(5L);

        ResponseEntity<Long> result = controller.getMyUnreadCount();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(5L, result.getBody());
    }

    @Test
    void shouldResolvePublicIdThenMarkAsReadAndReturn204() {
        UUID publicId = UUID.randomUUID();
        when(notificationService.resolveNotificationId(publicId)).thenReturn(42L);

        ResponseEntity<Void> result = controller.markAsRead(publicId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(notificationService).resolveNotificationId(publicId);
        verify(notificationService).markAsRead(42L);
    }

    @Test
    void shouldReturn204AfterMarkingAllAsRead() {
        ResponseEntity<Void> result = controller.markAllAsRead();

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(notificationService).markAllAsRead();
    }

    @Test
    void shouldRegisterSseEmitterForCurrentUser() {
        SseEmitter emitter = new SseEmitter();
        when(currentUser.getUsername()).thenReturn(USER_JDOE);
        when(sseEmitterRegistry.register(USER_JDOE)).thenReturn(emitter);

        SseEmitter result = controller.stream(currentUser);

        assertSame(emitter, result);
        verify(sseEmitterRegistry).register(USER_JDOE);
    }
}
