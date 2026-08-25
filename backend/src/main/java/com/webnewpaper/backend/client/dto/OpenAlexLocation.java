// OpenAlexLocation.java
package com.webnewpaper.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexLocation {
    private OpenAlexSource source;
}
