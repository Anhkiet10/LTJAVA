package com.webnewpaper.backend.entity;

import com.webnewpaper.backend.enums.SourceApi;
import com.webnewpaper.backend.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_sync_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiSyncLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "api_source", nullable = false, length = 30)
    private SourceApi apiSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SyncStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "records_synced")
    @Builder.Default
    private Integer recordsSynced = 0;
}