package com.webnewpaper.backend.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webnewpaper.backend.config.OpenAiProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {

    private final RestClient openAiRestClient;
    private final OpenAiProperties openAiProperties;

    public OpenAiClient(RestClient openAiRestClient, OpenAiProperties openAiProperties) {
        this.openAiRestClient = openAiRestClient;
        this.openAiProperties = openAiProperties;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingResponse {
        private List<EmbeddingData> data;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingData {
        private List<Double> embedding;
    }

    public float[] embed(String text) {
        int maxRetries = 5;
        long waitMillis = 5000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                EmbeddingResponse response = openAiRestClient.post()
                        .uri("/embeddings")
                        .body(Map.of("model", openAiProperties.getEmbeddingModel(), "input", text))
                        .retrieve()
                        .body(EmbeddingResponse.class);

                List<Double> vector = response.getData().get(0).getEmbedding();
                float[] result = new float[vector.size()];
                for (int i = 0; i < vector.size(); i++) result[i] = vector.get(i).floatValue();
                return result;

            } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
                if (attempt == maxRetries) throw e;
                try {
                    Thread.sleep(waitMillis * attempt); // chờ lâu hơn sau mỗi lần thử lại
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        throw new IllegalStateException("Không thể tạo embedding sau nhiều lần thử lại");
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatResponse {
        private List<ChatChoice> choices;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatChoice {
        private ChatMessage message;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatMessage {
        private String content;
    }

    public String chat(String systemPrompt, String userPrompt) {
        int maxRetries = 3;
        long waitMillis = 8000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ChatResponse response = openAiRestClient.post()
                        .uri("/chat/completions")
                        .body(Map.of(
                                "model", openAiProperties.getChatModel(),
                                "temperature", 0.3,
                                "messages", List.of(
                                        Map.of("role", "system", "content", systemPrompt),
                                        Map.of("role", "user", "content", userPrompt)
                                )
                        ))
                        .retrieve()
                        .body(ChatResponse.class);

                return response.getChoices().get(0).getMessage().getContent();

            } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
                if (attempt == maxRetries) throw e;
                try {
                    Thread.sleep(waitMillis * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        throw new IllegalStateException("Không thể sinh câu trả lời sau nhiều lần thử lại");
    }
}
