package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter @AllArgsConstructor
public class PaperSummaryResponse {
    private Long id;
    private String title;
    private Integer publicationYear;
    private String doi;
    private String journalName;
    private List<String> authorNames;
}