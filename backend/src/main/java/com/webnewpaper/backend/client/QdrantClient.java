package com.webnewpaper.backend.client;

import com.webnewpaper.backend.config.QdrantProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class QdrantClient {

    private final RestClient qdrantRestClient;
    private final QdrantProperties qdrantProperties;

    public QdrantClient(RestClient qdrantRestClient, QdrantProperties qdrantProperties) {
        this.qdrantRestClient = qdrantRestClient;
        this.qdrantProperties = qdrantProperties;
    }

    public void ensureCollection(int vectorSize) {
        try {
            qdrantRestClient.get()
                    .uri("/collections/{name}", qdrantProperties.getCollectionName())
                    .retrieve()
                    .body(Map.class);
        } catch (Exception notFound) {
            qdrantRestClient.put()
                    .uri("/collections/{name}", qdrantProperties.getCollectionName())
                    .body(Map.of("vectors", Map.of("size", vectorSize, "distance", "Cosine")))
                    .retrieve()
                    .body(Map.class);
        }
    }

    public record ChunkPoint(long pointId, float[] vector, Long paperId, String paperTitle, int chunkIndex, String content) {}

    public void upsertPoints(List<ChunkPoint> points) {
        List<Map<String, Object>> qdrantPoints = points.stream()
                .map(p -> Map.<String, Object>of(
                        "id", p.pointId(),
                        "vector", toFloatList(p.vector()),
                        "payload", Map.of(
                                "paper_id", p.paperId(),
                                "paper_title", p.paperTitle(),
                                "chunk_index", p.chunkIndex(),
                                "content", p.content()
                        )
                ))
                .collect(Collectors.toList());

        qdrantRestClient.put()
                .uri("/collections/{name}/points", qdrantProperties.getCollectionName())
                .body(Map.of("points", qdrantPoints))
                .retrieve()
                .body(Map.class);
    }

    public record SearchHit(double score, Long paperId, String paperTitle, int chunkIndex, String content) {}

    @SuppressWarnings("unchecked")
    public List<SearchHit> search(float[] queryVector, int limit, Long filterPaperId) {
        Map<String, Object> body = new HashMap<>();
        body.put("vector", toFloatList(queryVector));
        body.put("limit", limit);
        body.put("with_payload", true);

        if (filterPaperId != null) {
            body.put("filter", Map.of("must", List.of(
                    Map.of("key", "paper_id", "match", Map.of("value", filterPaperId))
            )));
        }

        Map<String, Object> response = qdrantRestClient.post()
                .uri("/collections/{name}/points/search", qdrantProperties.getCollectionName())
                .body(body)
                .retrieve()
                .body(Map.class);

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("result");
        List<SearchHit> hits = new ArrayList<>();

        for (Map<String, Object> result : results) {
            Map<String, Object> payload = (Map<String, Object>) result.get("payload");
            hits.add(new SearchHit(
                    ((Number) result.get("score")).doubleValue(),
                    ((Number) payload.get("paper_id")).longValue(),
                    (String) payload.get("paper_title"),
                    ((Number) payload.get("chunk_index")).intValue(),
                    (String) payload.get("content")
            ));
        }

        return hits;
    }

    private List<Float> toFloatList(float[] arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) list.add(f);
        return list;
    }
}
