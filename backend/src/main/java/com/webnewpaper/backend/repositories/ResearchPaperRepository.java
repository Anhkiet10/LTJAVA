// package com.webnewpaper.backend.repositories;

// import com.webnewpaper.backend.entity.ResearchPaper;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import java.util.Optional;

// public interface ResearchPaperRepository extends JpaRepository<ResearchPaper, Long> {

//     Optional<ResearchPaper> findByDoi(String doi);

//     // Tìm kiếm theo tiêu đề chứa từ khóa (UC-02), có phân trang
//     Page<ResearchPaper> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

//     // Đếm số bài báo theo năm cho 1 keyword cụ thể (dùng cho UC-08 - xu hướng công bố)
//     @Query("SELECT rp.publicationYear, COUNT(rp) FROM ResearchPaper rp " +
//            "JOIN rp.keywords k WHERE k.id = :keywordId GROUP BY rp.publicationYear ORDER BY rp.publicationYear")
//     java.util.List<Object[]> countByYearForKeyword(@Param("keywordId") Long keywordId);
//     @Query("SELECT DISTINCT p FROM ResearchPaper p " +
//         "LEFT JOIN p.authors a " +
//         "LEFT JOIN p.journal j " +
//         "WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//         "       OR LOWER(p.abstractText) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
//         "AND (:author IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :author, '%'))) " +
//         "AND (:journal IS NULL OR LOWER(j.name) LIKE LOWER(CONCAT('%', :journal, '%'))) " +
//         "AND (:year IS NULL OR p.publicationYear = :year)")
//     Page<ResearchPaper> search(@Param("keyword") String keyword,
//                                 @Param("author") String author,
//                                 @Param("journal") String journal,
//                                 @Param("year") Integer year,
//                                 Pageable pageable);
// }

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
    @Query("SELECT p FROM ResearchPaper p JOIN p.keywords k WHERE k.id = :keywordId AND p.abstractText IS NOT NULL ORDER BY p.publicationYear ASC")
    List<ResearchPaper> findPapersWithAbstractByKeyword(@Param("keywordId") Long keywordId, Pageable pageable);
}