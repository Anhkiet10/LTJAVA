package com.webnewpaper.backend.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webnewpaper.backend.config.OpenAiProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {

    private final RestClient openAiRestClient;
    private final OpenAiProperties openAiProperties;

    public OpenAiClient(
            RestClient openAiRestClient,
            OpenAiProperties openAiProperties) {

        this.openAiRestClient = openAiRestClient;
        this.openAiProperties = openAiProperties;
    }


    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingResponse {

        private List<EmbeddingData> data;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmbeddingData {

        private List<Double> embedding;
    }


    public float[] embed(String text) {

        int maxRetries = 5;
        long waitMillis = 5000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            try {


                Map<String, Object> requestBody = Map.of(
                        "model", openAiProperties.getEmbeddingModel(),
                        "input", text
                );


                EmbeddingResponse response = openAiRestClient
                        .post()
                        .uri("/embeddings")
                        .body(requestBody)
                        .retrieve()
                        .body(EmbeddingResponse.class);


                List<Double> vector = response
                        .getData()
                        .get(0)
                        .getEmbedding();


                float[] result = new float[vector.size()];

                for (int i = 0; i < vector.size(); i++) {

                    Double value = vector.get(i);

                    result[i] = value.floatValue();
                }

                return result;

            } catch (HttpClientErrorException.TooManyRequests e) {


                if (attempt == maxRetries) {
                    throw e;
                }

                try {


                    long currentWait = waitMillis * attempt;

                    Thread.sleep(currentWait);

                } catch (InterruptedException ie) {

                    Thread.currentThread().interrupt();

                    throw new RuntimeException(ie);
                }
            }
        }

        throw new IllegalStateException(
                "Không thể tạo embedding sau nhiều lần thử lại"
        );
    }



    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatResponse {

        private List<ChatChoice> choices;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatChoice {

        private ChatMessage message;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatMessage {

        private String content;
    }


    public String chat(String systemPrompt, String userPrompt) {

        int maxRetries = 3;
        long waitMillis = 8000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            try {

                Map<String, String> systemMessage = Map.of(
                        "role", "system",
                        "content", systemPrompt
                );

                Map<String, String> userMessage = Map.of(
                        "role", "user",
                        "content", userPrompt
                );

                List<Map<String, String>> messages = List.of(
                        systemMessage,
                        userMessage
                );

                Map<String, Object> requestBody = Map.of(
                        "model", openAiProperties.getChatModel(),
                        "temperature", 0.3,
                        "messages", messages
                );

                ChatResponse response = openAiRestClient
                        .post()
                        .uri("/chat/completions")
                        .body(requestBody)
                        .retrieve()
                        .body(ChatResponse.class);

                String result = response
                        .getChoices()
                        .get(0)
                        .getMessage()
                        .getContent();

                return result;

            } catch (HttpClientErrorException.TooManyRequests e) {

                if (attempt == maxRetries) {
                    throw e;
                }

                try {

                    long currentWait = waitMillis * attempt;

                    Thread.sleep(currentWait);

                } catch (InterruptedException ie) {

                    Thread.currentThread().interrupt();

                    throw new RuntimeException(ie);
                }
            }
        }

        throw new IllegalStateException(
                "Không thể sinh câu trả lời sau nhiều lần thử lại"
        );
    }
}
