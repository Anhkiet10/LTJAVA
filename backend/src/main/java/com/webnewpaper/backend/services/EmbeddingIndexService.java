package com.webnewpaper.backend.services;

import com.webnewpaper.backend.client.OpenAiClient;
import com.webnewpaper.backend.client.QdrantClient;
import com.webnewpaper.backend.entity.PaperFullText;
import com.webnewpaper.backend.entity.ResearchPaper;
import com.webnewpaper.backend.repositories.PaperFullTextRepository;
import com.webnewpaper.backend.repositories.ResearchPaperRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingIndexService {


    private static final int EMBEDDING_DIMENSIONS = 3072;
    private final PaperFullTextRepository fullTextRepository;
    private final ResearchPaperRepository paperRepository;
    private final MarkdownConverterService markdownConverterService;
    private final ChunkingService chunkingService;
    private final OpenAiClient openAiClient;
    private final QdrantClient qdrantClient;

    public EmbeddingIndexService(PaperFullTextRepository fullTextRepository, ResearchPaperRepository paperRepository,
                                  MarkdownConverterService markdownConverterService, ChunkingService chunkingService,
                                  OpenAiClient openAiClient, QdrantClient qdrantClient) {
        this.fullTextRepository = fullTextRepository;
        this.paperRepository = paperRepository;
        this.markdownConverterService = markdownConverterService;
        this.chunkingService = chunkingService;
        this.openAiClient = openAiClient;
        this.qdrantClient = qdrantClient;
    }

    public int indexPaper(Long paperId) {
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài báo"));
        PaperFullText fullText = fullTextRepository.findByPaperId(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Chưa có full-text, hãy trích xuất trước"));

        String markdown = markdownConverterService.toMarkdown(fullText.getRawText());
        List<ChunkingService.Chunk> chunks = chunkingService.chunkMarkdown(markdown);

        qdrantClient.ensureCollection(EMBEDDING_DIMENSIONS);

        List<QdrantClient.ChunkPoint> points = new ArrayList<>();
        for (ChunkingService.Chunk chunk : chunks) {
        float[] vector = openAiClient.embed(chunk.content());
        long pointId = paperId * 10000 + chunk.index();
        points.add(new QdrantClient.ChunkPoint(pointId, vector, paperId, paper.getTitle(), chunk.index(), chunk.content()));

        try {
            Thread.sleep(4500); // ~13 request/phút, dưới ngưỡng free tier (15 RPM)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

        qdrantClient.upsertPoints(points);

        paper.setAiIndexed(true);
        paperRepository.save(paper);

        return points.size();
    }
}