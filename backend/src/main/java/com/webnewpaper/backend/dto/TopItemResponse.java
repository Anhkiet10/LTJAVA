package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class TopItemResponse {
    private Long id;
    private String name;
    private long count;
}