package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class TrendingTopicResponse {
    private Long keywordId;
    private String name;
    private long paperCount;
}