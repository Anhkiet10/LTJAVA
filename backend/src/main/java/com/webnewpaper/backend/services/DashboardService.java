package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.DashboardSummaryResponse;
import com.webnewpaper.backend.dto.TopItemResponse;
import com.webnewpaper.backend.dto.YearCountResponse;
import com.webnewpaper.backend.entity.Journal;
import com.webnewpaper.backend.entity.Keyword;
import com.webnewpaper.backend.repositories.JournalRepository;
import com.webnewpaper.backend.repositories.KeywordRepository;
import com.webnewpaper.backend.repositories.ResearchPaperRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ResearchPaperRepository paperRepository;
    private final KeywordRepository keywordRepository;
    private final JournalRepository journalRepository;

    public DashboardService(ResearchPaperRepository paperRepository,
                             KeywordRepository keywordRepository, JournalRepository journalRepository) {
        this.paperRepository = paperRepository;
        this.keywordRepository = keywordRepository;
        this.journalRepository = journalRepository;
    }

    public DashboardSummaryResponse getSummary() {
        Pageable top5 = PageRequest.of(0, 5);

        List<TopItemResponse> topKeywords = keywordRepository.findTopKeywordsByPaperCount(top5).stream()
                .map(row -> new TopItemResponse(((Keyword) row[0]).getId(), ((Keyword) row[0]).getName(), (Long) row[1]))
                .collect(Collectors.toList());

        List<TopItemResponse> topJournals = journalRepository.findTopJournalsByPaperCount(top5).stream()
                .map(row -> new TopItemResponse(((Journal) row[0]).getId(), ((Journal) row[0]).getName(), (Long) row[1]))
                .collect(Collectors.toList());

        return new DashboardSummaryResponse(topKeywords, topJournals);
    }

    public List<YearCountResponse> getTrend(Long keywordId) {
        return paperRepository.countByYearForKeyword(keywordId).stream()
                .map(row -> new YearCountResponse((Integer) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }
}