package com.webnewpaper.backend.entity;

import com.webnewpaper.backend.enums.SourceApi;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "research_papers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResearchPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "abstract", columnDefinition = "TEXT")
    private String abstractText;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(unique = true, length = 150)
    private String doi;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_api", nullable = false, length = 30)
    private SourceApi sourceApi;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "journal_id")
    private Journal journal;

    @ManyToMany
    @JoinTable(
        name = "paper_author",
        joinColumns = @JoinColumn(name = "paper_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Builder.Default
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "paper_keyword",
        joinColumns = @JoinColumn(name = "paper_id"),
        inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    @Builder.Default
    private Set<Keyword> keywords = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    @Column(name = "oa_url")
    private String oaUrl;

    @Column(name = "full_text_extracted", nullable = false)
    @Builder.Default
    private boolean fullTextExtracted = false;
    @Column(name = "ai_indexed", nullable = false)
    @Builder.Default
    private boolean aiIndexed = false;
}
