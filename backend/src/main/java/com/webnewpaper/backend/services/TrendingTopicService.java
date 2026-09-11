package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.TrendingTopicResponse;
import com.webnewpaper.backend.repositories.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendingTopicService {
    private final KeywordRepository keywordRepository;

    public List<TrendingTopicResponse> getTrendingTopics(int limit) {
        return keywordRepository.findTopKeywordsByPaperCount(PageRequest.of(0, limit))
                .stream()
                .map(obj -> new TrendingTopicResponse((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }
}