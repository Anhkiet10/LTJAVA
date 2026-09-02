package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class BookmarkResponse {
    private Long id;
    private Long paperId;
    private String paperTitle;
    private LocalDateTime createdAt;
}