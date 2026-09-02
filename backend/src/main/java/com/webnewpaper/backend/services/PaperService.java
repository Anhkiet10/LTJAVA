package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.PageResponse;
import com.webnewpaper.backend.dto.PaperDetailResponse;
import com.webnewpaper.backend.dto.PaperSummaryResponse;
import com.webnewpaper.backend.entity.ResearchPaper;
import com.webnewpaper.backend.mapper.PaperMapper;
import com.webnewpaper.backend.repositories.ResearchPaperRepository;
import com.webnewpaper.backend.repositories.spec.ResearchPaperSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaperService {

    private final ResearchPaperRepository paperRepository;
    private final PaperMapper paperMapper;

    public PaperService(ResearchPaperRepository paperRepository, PaperMapper paperMapper) {
        this.paperRepository = paperRepository;
        this.paperMapper = paperMapper;
    }

    public PageResponse<PaperSummaryResponse> search(String keyword, String author, String journal,
                                                   Integer year, Long keywordId, Pageable pageable) {
    Page<ResearchPaper> results = paperRepository.findAll(
            ResearchPaperSpecifications.search(blankToNull(keyword), blankToNull(author), blankToNull(journal), year, keywordId),
            pageable);
    Page<PaperSummaryResponse> mapped = results.map(paperMapper::toSummary);
    return PageResponse.from(mapped);
    }

    public PaperDetailResponse getById(Long id) {
        ResearchPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài báo với id: " + id));
        return paperMapper.toDetail(paper);
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}