package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter @AllArgsConstructor
public class PaperDetailResponse {
    private Long id;
    private String title;
    private String abstractText;
    private Integer publicationYear;
    private String doi;
    private String sourceApi;
    private Long journalId;
    private String journalName;
    private String journalPublisher;
    private List<String> authorNames;
    private List<KeywordTagResponse> keywords;
    private String oaUrl;
    private boolean fullTextExtracted;
    private boolean aiIndexed;
    private LocalDateTime createdAt;
}