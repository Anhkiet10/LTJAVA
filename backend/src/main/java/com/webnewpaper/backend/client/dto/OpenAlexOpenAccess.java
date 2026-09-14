package com.webnewpaper.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexOpenAccess {
    @JsonProperty("is_oa")
    private Boolean isOa;

    @JsonProperty("oa_url")
    private String oaUrl;
}