package com.webnewpaper.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class BookmarkStatusResponse {
    private boolean bookmarked;
    private Long bookmarkId;
}