package com.webnewpaper.backend.services;

import com.webnewpaper.backend.client.OpenAiClient;
import com.webnewpaper.backend.client.QdrantClient;
import com.webnewpaper.backend.dto.AskResponse;
import com.webnewpaper.backend.dto.SourceChunkResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final int TOP_K = 5;

    private final OpenAiClient openAiClient;
    private final QdrantClient qdrantClient;

    public RagService(OpenAiClient openAiClient, QdrantClient qdrantClient) {
        this.openAiClient = openAiClient;
        this.qdrantClient = qdrantClient;
    }

    public AskResponse ask(String question, Long paperId) {
        float[] queryVector = openAiClient.embed(question);
        List<QdrantClient.SearchHit> hits = qdrantClient.search(queryVector, TOP_K, paperId);

        if (hits.isEmpty()) {
            return new AskResponse(
                    "Chưa có dữ liệu nào được lập chỉ mục để trả lời câu hỏi này.",
                    List.of());
        }

        String context = hits.stream()
                .map(h -> "[Bài: " + h.paperTitle() + "]\n" + h.content())
                .collect(Collectors.joining("\n\n---\n\n"));

        String systemPrompt = "Bạn là trợ lý nghiên cứu khoa học. Chỉ trả lời dựa trên nội dung được cung cấp bên dưới. " +
                "Nếu nội dung không đủ để trả lời, hãy nói rõ là không có đủ thông tin. Trả lời bằng tiếng Việt, ngắn gọn, chính xác.";
        String userPrompt = "Nội dung tham khảo:\n\n" + context + "\n\nCâu hỏi: " + question;

        String answer = openAiClient.chat(systemPrompt, userPrompt);

        List<SourceChunkResponse> sources = hits.stream()
                .map(h -> new SourceChunkResponse(h.paperId(), h.paperTitle(), h.chunkIndex(), h.score()))
                .collect(Collectors.toList());

        return new AskResponse(answer, sources);
    }
}