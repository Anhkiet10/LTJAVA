package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class SourceChunkResponse {
    private Long paperId;
    private String paperTitle;
    private int chunkIndex;
    private double score;
}
