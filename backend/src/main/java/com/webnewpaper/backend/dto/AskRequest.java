package com.webnewpaper.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AskRequest {
    private String question;
    private Long paperId; // optional — null nghĩa là tìm trong toàn bộ dữ liệu đã index
}
