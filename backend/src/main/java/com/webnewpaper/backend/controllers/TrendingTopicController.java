package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.TrendingTopicResponse;
import com.webnewpaper.backend.services.TrendingTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trending-topics")
@RequiredArgsConstructor
public class TrendingTopicController {
    private final TrendingTopicService trendingTopicService;

    @GetMapping
    public ResponseEntity<List<TrendingTopicResponse>> getTrendingTopics(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(trendingTopicService.getTrendingTopics(limit));
    }
}