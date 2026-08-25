package com.webnewpaper.backend.scheduler;

import com.webnewpaper.backend.services.PaperSyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import com.webnewpaper.backend.config.SyncProperties;
@Component
public class PaperSyncScheduler {

    private final PaperSyncService paperSyncService;
    private final SyncProperties syncProperties;

    public PaperSyncScheduler(PaperSyncService paperSyncService, SyncProperties syncProperties) {
        this.paperSyncService = paperSyncService;
        this.syncProperties = syncProperties;
    }

    @Scheduled(initialDelay = 6 * 60 * 60 * 1000, fixedRate = 6 * 60 * 60 * 1000)
    public void scheduledSync() {
        paperSyncService.syncFromOpenAlex(syncProperties.getSeedKeywords());
    }
}
