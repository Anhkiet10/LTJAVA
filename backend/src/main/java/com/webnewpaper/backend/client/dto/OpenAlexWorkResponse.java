package com.webnewpaper.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexWorkResponse {
    private List<OpenAlexWork> results;
}
