package com.webnewpaper.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "paper_full_text")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaperFullText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "paper_id", nullable = false, unique = true)
    private ResearchPaper paper;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(name = "extraction_method", length = 20)
    private String extractionMethod; // "PDFBOX" hoặc "OCR"

    @Column(name = "extracted_at", nullable = false)
    private LocalDateTime extractedAt;
}