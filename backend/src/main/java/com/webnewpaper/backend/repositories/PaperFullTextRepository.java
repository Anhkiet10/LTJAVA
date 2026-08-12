package com.webnewpaper.backend.repositories;

import com.webnewpaper.backend.entity.PaperFullText;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaperFullTextRepository extends JpaRepository<PaperFullText, Long> {
    Optional<PaperFullText> findByPaperId(Long paperId);
}