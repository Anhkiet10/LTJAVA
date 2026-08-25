package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.TrendingTopicResponse;
import com.webnewpaper.backend.services.TrendingTopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TrendingTopicController {

    private final TrendingTopicService trendingTopicService;

    public TrendingTopicController(TrendingTopicService trendingTopicService) {
        this.trendingTopicService = trendingTopicService;
    }

    @GetMapping("/api/trending-topics")
    public ResponseEntity<List<TrendingTopicResponse>> getTrendingTopics(
            @RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(trendingTopicService.getTopKeywords(limit));
    }
}