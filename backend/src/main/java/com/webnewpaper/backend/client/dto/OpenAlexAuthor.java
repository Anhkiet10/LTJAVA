// OpenAlexAuthor.java
package com.webnewpaper.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexAuthor {
    @JsonProperty("display_name")
    private String displayName;
}
