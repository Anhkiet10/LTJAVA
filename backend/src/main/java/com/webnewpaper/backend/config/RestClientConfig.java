package com.webnewpaper.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
public class RestClientConfig {

    @Value("${app.sync.openalex-base-url}")
    private String openAlexBaseUrl;

    @Bean
    public RestClient openAlexRestClient() {
        return RestClient.builder()
                .baseUrl(openAlexBaseUrl)
                .build();
    }

    @Bean
    public RestClient genericRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL) // tự động theo redirect (301/302)
                .build();

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }
    @Bean
    public RestClient openAiRestClient(OpenAiProperties openAiProperties) {
        return RestClient.builder()
                .baseUrl(openAiProperties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + openAiProperties.getApiKey())
                .build();
    }

    @Bean
    public RestClient qdrantRestClient(QdrantProperties qdrantProperties) {
        return RestClient.builder()
                .baseUrl(qdrantProperties.getBaseUrl())
                .build();
    }
}
