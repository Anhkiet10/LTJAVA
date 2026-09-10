package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter @AllArgsConstructor
public class DashboardSummaryResponse {
    private List<TopItemResponse> topKeywords;
    private List<TopItemResponse> topJournals;
}