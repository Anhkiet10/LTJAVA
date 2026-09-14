package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter @AllArgsConstructor
public class AskResponse {
    private String answer;
    private List<SourceChunkResponse> sources;
}
