package com.webnewpaper.backend.repositories;

import com.webnewpaper.backend.entity.Follow;
import com.webnewpaper.backend.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByUserId(Long userId);
    Optional<Follow> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
    List<Follow> findByTargetTypeAndTargetId(TargetType targetType, Long targetId);
}