package com.webnewpaper.backend.repositories;

import com.webnewpaper.backend.entity.ApiSyncLog;
import com.webnewpaper.backend.enums.SourceApi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApiSyncLogRepository extends JpaRepository<ApiSyncLog, Long> {
    List<ApiSyncLog> findByApiSourceOrderByStartedAtDesc(SourceApi apiSource);
}