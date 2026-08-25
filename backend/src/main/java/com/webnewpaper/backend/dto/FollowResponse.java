package com.webnewpaper.backend.dto;

import com.webnewpaper.backend.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class FollowResponse {
    private Long id;
    private TargetType targetType;
    private Long targetId;
    private String targetName;
}