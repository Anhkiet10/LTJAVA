package com.webnewpaper.backend.services;

import com.webnewpaper.backend.client.dto.OpenAlexAuthorship;
import com.webnewpaper.backend.client.dto.OpenAlexConcept;
import com.webnewpaper.backend.client.dto.OpenAlexWork;
import com.webnewpaper.backend.entity.*;
import com.webnewpaper.backend.enums.SourceApi;
import com.webnewpaper.backend.repositories.*;
import com.webnewpaper.backend.utils.AbstractReconstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PaperPersistenceService {

    private final ResearchPaperRepository paperRepository;
    private final AuthorRepository authorRepository;
    private final JournalRepository journalRepository;
    private final KeywordRepository keywordRepository;

    public PaperPersistenceService(ResearchPaperRepository paperRepository, AuthorRepository authorRepository,
                                    JournalRepository journalRepository, KeywordRepository keywordRepository) {
        this.paperRepository = paperRepository;
        this.authorRepository = authorRepository;
        this.journalRepository = journalRepository;
        this.keywordRepository = keywordRepository;
    }

    @Transactional
    public ResearchPaper saveWorkIfNotExists(OpenAlexWork work) {
        if (work.getDoi() == null || work.getTitle() == null || work.getPublicationYear() == null) {
            return null;
        }

        String cleanDoi = work.getDoi().replace("https://doi.org/", "");
        if (paperRepository.findByDoi(cleanDoi).isPresent()) {
            return null;
        }

        ResearchPaper paper = ResearchPaper.builder()
                .title(work.getTitle())
                .abstractText(AbstractReconstructor.reconstruct(work.getAbstractInvertedIndex()))
                .publicationYear(work.getPublicationYear())
                .doi(cleanDoi)
                .sourceApi(SourceApi.OPENALEX)
                .journal(resolveJournal(work))
                .authors(new HashSet<>(resolveAuthors(work).values()))
                .keywords(new HashSet<>(resolveKeywords(work).values()))
                .oaUrl(work.getOpenAccess() != null ? work.getOpenAccess().getOaUrl() : null)
                .build();
                

        return paperRepository.save(paper);
    }

    private Journal resolveJournal(OpenAlexWork work) {
        if (work.getPrimaryLocation() == null || work.getPrimaryLocation().getSource() == null) return null;
        String name = work.getPrimaryLocation().getSource().getDisplayName();
        if (name == null) return null;

        return journalRepository.findByName(name).orElseGet(() -> journalRepository.save(
                Journal.builder()
                        .name(name)
                        .publisher(work.getPrimaryLocation().getSource().getHostOrganizationName())
                        .issn(work.getPrimaryLocation().getSource().getIssnL())
                        .build()));
    }

    // Dùng Map<tên, Author> để loại trùng chắc chắn — OpenAlex đôi khi liệt kê
    // cùng 1 tác giả nhiều lần trong "authorships" (do nhiều affiliation khác nhau)
    private Map<String, Author> resolveAuthors(OpenAlexWork work) {
        Map<String, Author> authors = new LinkedHashMap<>();
        if (work.getAuthorships() == null) return authors;

        for (OpenAlexAuthorship authorship : work.getAuthorships()) {
            if (authorship.getAuthor() == null || authorship.getAuthor().getDisplayName() == null) continue;
            String fullName = authorship.getAuthor().getDisplayName();
            if (authors.containsKey(fullName)) continue;

            Author author = authorRepository.findByFullName(fullName)
                    .orElseGet(() -> authorRepository.save(Author.builder().fullName(fullName).build()));
            authors.put(fullName, author);
        }
        return authors;
    }

    private Map<String, Keyword> resolveKeywords(OpenAlexWork work) {
        Map<String, Keyword> keywords = new LinkedHashMap<>();
        if (work.getConcepts() == null) return keywords;

        for (OpenAlexConcept concept : work.getConcepts()) {
            if (concept.getDisplayName() == null) continue;
            if (keywords.containsKey(concept.getDisplayName())) continue;

            Keyword keyword = keywordRepository.findByName(concept.getDisplayName())
                    .orElseGet(() -> keywordRepository.save(Keyword.builder()
                            .name(concept.getDisplayName())
                            .createdAt(LocalDateTime.now())
                            .build()));
            keywords.put(concept.getDisplayName(), keyword);
        }
        return keywords;
    }
}
