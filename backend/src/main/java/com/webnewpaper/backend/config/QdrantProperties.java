package com.webnewpaper.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "app.qdrant")
public class QdrantProperties {
    private String baseUrl;
    private String collectionName;
}