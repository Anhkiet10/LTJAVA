package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.services.PaperSyncService;
import com.webnewpaper.backend.config.SyncProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/sync")
public class AdminSyncController {

    private final PaperSyncService paperSyncService;
    private final SyncProperties syncProperties;

    public AdminSyncController(PaperSyncService paperSyncService, SyncProperties syncProperties) {
        this.paperSyncService = paperSyncService;
        this.syncProperties = syncProperties;
    }

    @PostMapping("/{source}")
    public ResponseEntity<String> triggerSync(@PathVariable String source) {
        if (!"openalex".equalsIgnoreCase(source)) {
            return ResponseEntity.badRequest().body("Hiện chỉ hỗ trợ 'openalex'. Semantic Scholar sẽ bổ sung sau.");
        }
        try {
            int synced = paperSyncService.syncFromOpenAlex(syncProperties.getSeedKeywords());
            return ResponseEntity.ok("Đã đồng bộ xong. Số bài báo mới: " + synced);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đồng bộ thất bại: " + e.getMessage());
        }
    }
}
