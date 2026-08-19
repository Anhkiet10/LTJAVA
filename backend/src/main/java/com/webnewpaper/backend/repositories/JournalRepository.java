package com.webnewpaper.backend.repositories;

import com.webnewpaper.backend.entity.Journal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    Optional<Journal> findByName(String name);
    Optional<Journal> findByIssn(String issn);
    @Query("SELECT j, COUNT(p) FROM Journal j JOIN j.papers p GROUP BY j ORDER BY COUNT(p) DESC")
    List<Object[]> findTopJournalsByPaperCount(Pageable pageable);
}
