package com.credit.bladi.controller.notification;

import static com.credit.bladi.controller.notification.mapper.NotificationMapper.toCreditPage;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.controller.dto.notification.response.NotificationResponseDTO;
import com.credit.bladi.infrastructure.notification.SseEmitterRegistry;
import com.credit.bladi.service.notification.INotificationService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Validated
@RestController
@RequestMapping("/api/v1/notifications")
@PreAuthorize("hasRole('MANAGER')")
public class NotificationController implements INotificationController {
    private final INotificationService notificationService;

    private final SseEmitterRegistry sseEmitterRegistry;

    public NotificationController(INotificationService notificationService, SseEmitterRegistry sseEmitterRegistry) {
        this.notificationService = notificationService;
        this.sseEmitterRegistry = sseEmitterRegistry;
    }

    @Override
    @GetMapping
    public ResponseEntity<CreditPage<NotificationResponseDTO>> getMyNotifications(
        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
        @RequestParam(name = "size", defaultValue = "20", required = false) Integer size
    ) {
        return ResponseEntity.ok(toCreditPage(notificationService.getMyNotifications(page, size)));
    }

    @Override
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getMyUnreadCount() {
        return ResponseEntity.ok(notificationService.getMyUnreadCount());
    }

    @Override
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        Long resolvedId = notificationService.resolveNotificationId(id);
        notificationService.markAsRead(resolvedId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal UserDetails currentUser) {
        return sseEmitterRegistry.register(currentUser.getUsername());
    }
}
