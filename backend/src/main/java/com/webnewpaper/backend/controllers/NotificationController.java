package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.NotificationResponse;
import com.webnewpaper.backend.dto.PageResponse;
import com.webnewpaper.backend.security.CurrentUser;
import com.webnewpaper.backend.services.NotificationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUser currentUser;

    public NotificationController(NotificationService notificationService, CurrentUser currentUser) {
        this.notificationService = notificationService;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<NotificationResponse>> myNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificationService.listMine(currentUser.getUserId(jwt), pageable));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        notificationService.markAsRead(currentUser.getUserId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(notificationService.countUnread(currentUser.getUserId(jwt)));
    }
}