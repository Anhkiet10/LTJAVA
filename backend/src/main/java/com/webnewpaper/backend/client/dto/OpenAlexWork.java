// OpenAlexWork.java
package com.webnewpaper.backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAlexWork {
    private String id;
    private String doi;
    private String title;

    @JsonProperty("publication_year")
    private Integer publicationYear;

    @JsonProperty("abstract_inverted_index")
    private Map<String, List<Integer>> abstractInvertedIndex;

    private List<OpenAlexAuthorship> authorships;

    @JsonProperty("primary_location")
    private OpenAlexLocation primaryLocation;

    private List<OpenAlexConcept> concepts;
    @JsonProperty("open_access")
    private OpenAlexOpenAccess openAccess;
}
