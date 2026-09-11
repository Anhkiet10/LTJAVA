package com.webnewpaper.backend.dto;

import com.webnewpaper.backend.enums.TargetType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FollowRequest {
    private TargetType targetType;
    private Long targetId;
}