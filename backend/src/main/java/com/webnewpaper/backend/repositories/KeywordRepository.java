package com.webnewpaper.backend.repositories;

import com.webnewpaper.backend.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByName(String name);
    boolean existsByName(String name);
    @Query("SELECT k, COUNT(p) FROM Keyword k JOIN k.papers p GROUP BY k ORDER BY COUNT(p) DESC")
    List<Object[]> findTopKeywordsByPaperCount(Pageable pageable);
}