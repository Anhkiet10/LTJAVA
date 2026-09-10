package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class YearCountResponse {
    private Integer year;
    private Long count;
}