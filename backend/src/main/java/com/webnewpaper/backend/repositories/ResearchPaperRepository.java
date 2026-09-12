package com.webnewpaper.backend.repositories;

import com.webnewpaper.backend.entity.ResearchPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long>, JpaSpecificationExecutor<ResearchPaper> {

    
    Optional<ResearchPaper> findByDoi(String doi);

    
    @Query("SELECT rp.publicationYear, COUNT(rp) FROM ResearchPaper rp " +
           "JOIN rp.keywords k WHERE k.id = :keywordId GROUP BY rp.publicationYear ORDER BY rp.publicationYear")
    List<Object[]> countByYearForKeyword(@Param("keywordId") Long keywordId);

    
    @Query("SELECT p FROM ResearchPaper p JOIN p.keywords k WHERE k.id = :keywordId AND p.abstractText IS NOT NULL ORDER BY p.publicationYear DESC")
    List<ResearchPaper> findPapersWithAbstractByKeyword(@Param("keywordId") Long keywordId, Pageable pageable);

    
    @Query("SELECT DISTINCT p FROM ResearchPaper p JOIN p.authors a WHERE LOWER(a.fullName) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<ResearchPaper> findByAuthorNameContaining(@Param("authorName") String authorName);

    
    @Query("SELECT COUNT(p) FROM ResearchPaper p JOIN p.keywords k WHERE k.id = :keywordId")
    long countByKeywordId(@Param("keywordId") Long keywordId);
}