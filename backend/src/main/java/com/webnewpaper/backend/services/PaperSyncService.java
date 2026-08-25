package com.webnewpaper.backend.services;

import com.webnewpaper.backend.client.OpenAlexClient;
import com.webnewpaper.backend.client.dto.OpenAlexWork;
import com.webnewpaper.backend.entity.ApiSyncLog;
import com.webnewpaper.backend.entity.ResearchPaper;
import com.webnewpaper.backend.enums.SourceApi;
import com.webnewpaper.backend.enums.SyncStatus;
import com.webnewpaper.backend.repositories.ApiSyncLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaperSyncService {

    private static final Logger log = LoggerFactory.getLogger(PaperSyncService.class);

    private final OpenAlexClient openAlexClient;
    private final PaperPersistenceService paperPersistenceService;
    private final ApiSyncLogRepository syncLogRepository;
    private final NotificationService notificationService;

    public PaperSyncService(OpenAlexClient openAlexClient, PaperPersistenceService paperPersistenceService,
                             ApiSyncLogRepository syncLogRepository, NotificationService notificationService) {
        this.openAlexClient = openAlexClient;
        this.paperPersistenceService = paperPersistenceService;
        this.syncLogRepository = syncLogRepository;
        this.notificationService = notificationService;
    }

    public int syncFromOpenAlex(List<String> seedKeywords) {
        ApiSyncLog syncLog = syncLogRepository.save(ApiSyncLog.builder()
                .apiSource(SourceApi.OPENALEX)
                .status(SyncStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .recordsSynced(0)
                .build());

        List<ResearchPaper> newPapers = new ArrayList<>();
        boolean hasFatalError = false;
        try {
            for (String keyword : seedKeywords) {
                List<OpenAlexWork> works = openAlexClient.searchWorks(keyword);
                log.info("Từ khóa '{}': OpenAlex trả về {} kết quả", keyword, works.size());
                for (OpenAlexWork work : works) {
                    try {
                        ResearchPaper saved = paperPersistenceService.saveWorkIfNotExists(work);
                        if (saved != null) newPapers.add(saved);
                    } catch (Exception e) {
                        log.warn("Bỏ qua 1 bài báo do lỗi: {}", e.getMessage());
                    }
                }
            }
            notificationService.generateSyncNotifications(newPapers);
            syncLog.setStatus(SyncStatus.SUCCESS);
        } catch (Exception e) {
            log.error("Đồng bộ OpenAlex thất bại", e);
            syncLog.setStatus(SyncStatus.FAILED);
            hasFatalError = true;
        } finally {
            syncLog.setFinishedAt(LocalDateTime.now());
            syncLog.setRecordsSynced(newPapers.size());
            syncLogRepository.save(syncLog);
        }

        if (hasFatalError) throw new RuntimeException("Đồng bộ thất bại, xem log server để biết chi tiết");
        return newPapers.size();
    }
}
