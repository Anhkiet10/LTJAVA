package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.SyncLogResponse;
import com.webnewpaper.backend.entity.ApiSyncLog;
import com.webnewpaper.backend.repositories.ApiSyncLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/sync-logs")
public class AdminSyncLogController {

    private final ApiSyncLogRepository syncLogRepository;

    public AdminSyncLogController(ApiSyncLogRepository syncLogRepository) {
        this.syncLogRepository = syncLogRepository;
    }

    @GetMapping
    public ResponseEntity<List<SyncLogResponse>> list() {
        List<SyncLogResponse> logs = syncLogRepository.findAll(Sort.by(Sort.Direction.DESC, "startedAt"))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    private SyncLogResponse toResponse(ApiSyncLog log) {
        return new SyncLogResponse(log.getId(), log.getApiSource().name(), log.getStatus().name(),
                log.getStartedAt(), log.getFinishedAt(), log.getRecordsSynced());
    }
}