package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.DashboardSummaryResponse;
import com.webnewpaper.backend.dto.YearCountResponse;
import com.webnewpaper.backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> summary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/api/trends")
    public ResponseEntity<List<YearCountResponse>> trends(@RequestParam Long keywordId) {
        return ResponseEntity.ok(dashboardService.getTrend(keywordId));
    }
}