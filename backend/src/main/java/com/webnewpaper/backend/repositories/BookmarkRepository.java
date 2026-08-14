package com.webnewpaper.backend.repositories;

import com.webnewpaper.backend.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    Optional<Bookmark> findByUserIdAndPaperId(Long userId, Long paperId);
    boolean existsByUserIdAndPaperId(Long userId, Long paperId);
}