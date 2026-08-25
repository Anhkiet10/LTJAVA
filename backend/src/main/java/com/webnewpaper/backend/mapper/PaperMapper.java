package com.webnewpaper.backend.mapper;

import com.webnewpaper.backend.dto.KeywordTagResponse;
import com.webnewpaper.backend.dto.PaperDetailResponse;
import com.webnewpaper.backend.dto.PaperSummaryResponse;
import com.webnewpaper.backend.entity.Author;
import com.webnewpaper.backend.entity.Keyword;
import com.webnewpaper.backend.entity.ResearchPaper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaperMapper {

    public PaperSummaryResponse toSummary(ResearchPaper paper) {
        return new PaperSummaryResponse(
                paper.getId(),
                paper.getTitle(),
                paper.getPublicationYear(),
                paper.getDoi(),
                paper.getJournal() != null ? paper.getJournal().getName() : null,
                paper.getAuthors().stream().map(Author::getFullName).collect(Collectors.toList())
        );
    }

    public PaperDetailResponse toDetail(ResearchPaper paper) {
        List<String> authorNames = paper.getAuthors().stream()
                .map(Author::getFullName).collect(Collectors.toList());
        List<KeywordTagResponse> keywords = paper.getKeywords().stream()
                .map(k -> new KeywordTagResponse(k.getId(), k.getName()))
                .collect(Collectors.toList());

        return new PaperDetailResponse(
                paper.getId(),
                paper.getTitle(),
                paper.getAbstractText(),
                paper.getPublicationYear(),
                paper.getDoi(),
                paper.getSourceApi().name(),
                paper.getJournal() != null ? paper.getJournal().getId() : null,
                paper.getJournal() != null ? paper.getJournal().getName() : null,
                paper.getJournal() != null ? paper.getJournal().getPublisher() : null,
                authorNames,
                keywords,
                paper.getOaUrl(),
                paper.isFullTextExtracted(),
                paper.isAiIndexed(),
                paper.getCreatedAt()
        );
    }
}