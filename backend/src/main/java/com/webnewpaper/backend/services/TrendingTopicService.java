package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.TrendingTopicResponse;
import com.webnewpaper.backend.entity.Keyword;
import com.webnewpaper.backend.repositories.KeywordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrendingTopicService {

    private final KeywordRepository keywordRepository;

    public TrendingTopicService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public List<TrendingTopicResponse> getTopKeywords(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> rows = keywordRepository.findTopKeywordsByPaperCount(pageable);

        return rows.stream()
                .map(row -> {
                    Keyword keyword = (Keyword) row[0];
                    Long count = (Long) row[1];
                    return new TrendingTopicResponse(keyword.getId(), keyword.getName(), count);
                })
                .collect(Collectors.toList());
    }
}