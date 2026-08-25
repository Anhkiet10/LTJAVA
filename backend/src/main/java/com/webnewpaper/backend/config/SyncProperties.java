package com.webnewpaper.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "app.sync")
public class SyncProperties {
    private String contactEmail;
    private String openalexBaseUrl;
    private int perPage;
    private List<String> seedKeywords;
}
