package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class GapAnalysisResponse {
    private String keywordName;
    private int paperCount;
    private int yearFrom;
    private int yearTo;
    private String analysis;
}