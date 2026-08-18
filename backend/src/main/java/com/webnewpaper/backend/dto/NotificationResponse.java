package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String content;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;
}