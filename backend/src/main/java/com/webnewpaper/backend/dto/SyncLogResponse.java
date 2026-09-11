package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class SyncLogResponse {
    private Long id;
    private String apiSource;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer recordsSynced;
}