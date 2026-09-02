package com.webnewpaper.backend.client;

import com.webnewpaper.backend.client.dto.OpenAlexWork;
import com.webnewpaper.backend.client.dto.OpenAlexWorkResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.List;
import com.webnewpaper.backend.config.SyncProperties;
@Component
public class OpenAlexClient {

    private final RestClient openAlexRestClient;
    private final SyncProperties syncProperties;

    public OpenAlexClient(RestClient openAlexRestClient, SyncProperties syncProperties) {
        this.openAlexRestClient = openAlexRestClient;
        this.syncProperties = syncProperties;
    }

    public List<OpenAlexWork> searchWorks(String keyword) {
        OpenAlexWorkResponse response = openAlexRestClient.get()
                .uri("/works?search={keyword}&per-page={perPage}&mailto={email}",
                        keyword, syncProperties.getPerPage(), syncProperties.getContactEmail())
                .retrieve()
                .body(OpenAlexWorkResponse.class);

        return response != null && response.getResults() != null
                ? response.getResults()
                : List.of();
    }
}
