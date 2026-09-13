package com.webnewpaper.backend.services;

import com.webnewpaper.backend.entity.PaperFullText;
import com.webnewpaper.backend.entity.ResearchPaper;
import com.webnewpaper.backend.repositories.PaperFullTextRepository;
import com.webnewpaper.backend.repositories.ResearchPaperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FullTextService {

    private final ResearchPaperRepository paperRepository;
    private final PaperFullTextRepository fullTextRepository;
    private final TextExtractionService textExtractionService;

    public FullTextService(ResearchPaperRepository paperRepository, PaperFullTextRepository fullTextRepository,
                            TextExtractionService textExtractionService) {
        this.paperRepository = paperRepository;
        this.fullTextRepository = fullTextRepository;
        this.textExtractionService = textExtractionService;
    }

    @Transactional
    public String extractForPaper(Long paperId) throws Exception {
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài báo"));

        if (paper.getOaUrl() == null) {
            throw new IllegalArgumentException("Bài báo này không có oa_url (không phải open access)");
        }

        TextExtractionService.ExtractionResult result = textExtractionService.extract(paper.getOaUrl());

        PaperFullText fullText = fullTextRepository.findByPaperId(paperId)
                .orElse(PaperFullText.builder().paper(paper).build());
        fullText.setRawText(result.text());
        fullText.setExtractionMethod(result.method());
        fullText.setExtractedAt(LocalDateTime.now());
        fullTextRepository.save(fullText);

        paper.setFullTextExtracted(true);
        paperRepository.save(paper);

        return result.method() + " — " + result.text().length() + " ký tự";
    }
}
