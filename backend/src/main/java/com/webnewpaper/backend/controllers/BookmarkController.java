package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.entity.PaperFullText;
import com.webnewpaper.backend.repositories.PaperFullTextRepository;
import com.webnewpaper.backend.services.ChunkingService;
import com.webnewpaper.backend.services.EmbeddingIndexService;
import com.webnewpaper.backend.services.FullTextService;
import com.webnewpaper.backend.services.MarkdownConverterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/full-text")
public class AdminFullTextController {

    private final FullTextService fullTextService;
    private final PaperFullTextRepository paperFullTextRepository;
    private final MarkdownConverterService markdownConverterService;
    private final ChunkingService chunkingService;
    private final EmbeddingIndexService embeddingIndexService; 

    public AdminFullTextController(FullTextService fullTextService, PaperFullTextRepository paperFullTextRepository,
                                    MarkdownConverterService markdownConverterService, ChunkingService chunkingService,EmbeddingIndexService embeddingIndexService) {
        this.fullTextService = fullTextService;
        this.paperFullTextRepository = paperFullTextRepository;
        this.markdownConverterService = markdownConverterService;
        this.chunkingService = chunkingService;
        this.embeddingIndexService = embeddingIndexService;
    }

    @PostMapping("/{paperId}")
    public ResponseEntity<String> extract(@PathVariable Long paperId) {
        try {
            return ResponseEntity.ok(fullTextService.extractForPaper(paperId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }
    @PostMapping("/{paperId}/index")
    public ResponseEntity<String> index(@PathVariable Long paperId) {
        try {
            int count = embeddingIndexService.indexPaper(paperId);
            return ResponseEntity.ok("Đã index " + count + " chunk vào Qdrant.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }
    @PostMapping("/{paperId}/process")
    public ResponseEntity<String> process(@PathVariable Long paperId) {
        try {
            String extractResult = fullTextService.extractForPaper(paperId);
            int count = embeddingIndexService.indexPaper(paperId);
            return ResponseEntity.ok("Trích xuất: " + extractResult + " | Đã index " + count + " chunk.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }
    @GetMapping("/{paperId}/chunks-preview")
    public ResponseEntity<Map<String, Object>> previewChunks(@PathVariable Long paperId) {
        PaperFullText fullText = paperFullTextRepository.findByPaperId(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Chưa có full-text cho bài này, hãy trích xuất trước"));

        String markdown = markdownConverterService.toMarkdown(fullText.getRawText());
        List<ChunkingService.Chunk> chunks = chunkingService.chunkMarkdown(markdown);

        List<Map<String, Object>> preview = chunks.stream()
                .map(c -> Map.<String, Object>of(
                        "index", c.index(),
                        "charCount", c.content().length(),
                        "preview", c.content().substring(0, Math.min(150, c.content().length())) + "..."
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("totalChunks", chunks.size(), "chunks", preview));
    }
    
}