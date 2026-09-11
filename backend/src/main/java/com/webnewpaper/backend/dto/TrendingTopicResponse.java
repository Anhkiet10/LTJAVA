package com.webnewpaper.backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrendingTopicResponse {
    private String name;
    private Long paperCount;
}