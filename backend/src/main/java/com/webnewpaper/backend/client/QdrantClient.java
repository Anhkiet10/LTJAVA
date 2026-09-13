package com.webnewpaper.backend.client;

import com.webnewpaper.backend.config.QdrantProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QdrantClient {

    private final RestClient qdrantRestClient;
    private final QdrantProperties qdrantProperties;

    public QdrantClient(
            RestClient qdrantRestClient,
            QdrantProperties qdrantProperties) {

        this.qdrantRestClient = qdrantRestClient;
        this.qdrantProperties = qdrantProperties;
    }


    public void ensureCollection(int vectorSize) {

        String collectionName =
                qdrantProperties.getCollectionName();

        try {

            qdrantRestClient
                    .get()
                    .uri("/collections/{name}", collectionName)
                    .retrieve()
                    .body(Map.class);

        } catch (Exception e) {


            Map<String, Object> vectorConfig = new HashMap<>();

            vectorConfig.put("size", vectorSize);
            vectorConfig.put("distance", "Cosine");

            Map<String, Object> requestBody = new HashMap<>();

            requestBody.put("vectors", vectorConfig);

            qdrantRestClient
                    .put()
                    .uri("/collections/{name}", collectionName)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);
        }
    }


    public record ChunkPoint(
            long pointId,
            float[] vector,
            Long paperId,
            String paperTitle,
            int chunkIndex,
            String content) {
    }


    public void upsertPoints(List<ChunkPoint> points) {

        List<Map<String, Object>> qdrantPoints =
                new ArrayList<>();

        for (ChunkPoint point : points) {

            Map<String, Object> payload =
                    new HashMap<>();

            payload.put("paper_id", point.paperId());
            payload.put("paper_title", point.paperTitle());
            payload.put("chunk_index", point.chunkIndex());
            payload.put("content", point.content());

            Map<String, Object> qdrantPoint =
                    new HashMap<>();

            qdrantPoint.put(
                    "id",
                    point.pointId()
            );

            qdrantPoint.put(
                    "vector",
                    toFloatList(point.vector())
            );

            qdrantPoint.put(
                    "payload",
                    payload
            );

            qdrantPoints.add(qdrantPoint);
        }

        Map<String, Object> requestBody =
                new HashMap<>();

        requestBody.put(
                "points",
                qdrantPoints
        );

        String collectionName =
                qdrantProperties.getCollectionName();

        qdrantRestClient
                .put()
                .uri(
                        "/collections/{name}/points",
                        collectionName
                )
                .body(requestBody)
                .retrieve()
                .body(Map.class);
    }

    public record SearchHit(
            double score,
            Long paperId,
            String paperTitle,
            int chunkIndex,
            String content) {
    }


    @SuppressWarnings("unchecked")
    public List<SearchHit> search(
            float[] queryVector,
            int limit,
            Long filterPaperId) {

        Map<String, Object> requestBody =
                new HashMap<>();

        List<Float> vector =
                toFloatList(queryVector);

        requestBody.put(
                "vector",
                vector
        );

        requestBody.put(
                "limit",
                limit
        );

        requestBody.put(
                "with_payload",
                true
        );

        if (filterPaperId != null) {

            Map<String, Object> match =
                    Map.of(
                            "value",
                            filterPaperId
                    );

            Map<String, Object> condition =
                    Map.of(
                            "key",
                            "paper_id",
                            "match",
                            match
                    );

            Map<String, Object> filter =
                    Map.of(
                            "must",
                            List.of(condition)
                    );

            requestBody.put(
                    "filter",
                    filter
            );
        }

        String collectionName =
                qdrantProperties.getCollectionName();

        Map<String, Object> response =
                qdrantRestClient
                        .post()
                        .uri(
                                "/collections/{name}/points/search",
                                collectionName
                        )
                        .body(requestBody)
                        .retrieve()
                        .body(Map.class);

        List<Map<String, Object>> results =
                (List<Map<String, Object>>)
                        response.get("result");

        List<SearchHit> hits =
                new ArrayList<>();

        for (Map<String, Object> result : results) {

            Map<String, Object> payload =
                    (Map<String, Object>)
                            result.get("payload");

            double score =
                    ((Number) result.get("score"))
                            .doubleValue();

            Long paperId =
                    ((Number) payload.get("paper_id"))
                            .longValue();

            String paperTitle =
                    (String) payload.get("paper_title");

            int chunkIndex =
                    ((Number) payload.get("chunk_index"))
                            .intValue();

            String content =
                    (String) payload.get("content");

            SearchHit hit =
                    new SearchHit(
                            score,
                            paperId,
                            paperTitle,
                            chunkIndex,
                            content
                    );

            hits.add(hit);
        }

        return hits;
    }


    private List<Float> toFloatList(float[] arr) {

        List<Float> list =
                new ArrayList<>();

        for (float value : arr) {

            list.add(value);
        }

        return list;
    }
}
