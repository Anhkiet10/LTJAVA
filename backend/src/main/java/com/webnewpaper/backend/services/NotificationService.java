package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.NotificationResponse;
import com.webnewpaper.backend.dto.PageResponse;
import com.webnewpaper.backend.entity.*;
import com.webnewpaper.backend.enums.TargetType;
import com.webnewpaper.backend.repositories.FollowRepository;
import com.webnewpaper.backend.repositories.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;

    public NotificationService(NotificationRepository notificationRepository, FollowRepository followRepository) {
        this.notificationRepository = notificationRepository;
        this.followRepository = followRepository;
    }

    @Transactional
    public void generateSyncNotifications(List<ResearchPaper> newPapers) {
        if (newPapers.isEmpty()) return;

        Map<Long, User> userMap = new HashMap<>();
        Map<Long, Set<Long>> userToMatchedPaperIds = new HashMap<>();

        for (ResearchPaper paper : newPapers) {
            if (paper.getJournal() != null) {
                for (Follow f : followRepository.findByTargetTypeAndTargetId(TargetType.JOURNAL, paper.getJournal().getId())) {
                    registerMatch(userMap, userToMatchedPaperIds, f, paper);
                }
            }
            for (Keyword keyword : paper.getKeywords()) {
                for (Follow f : followRepository.findByTargetTypeAndTargetId(TargetType.KEYWORD, keyword.getId())) {
                    registerMatch(userMap, userToMatchedPaperIds, f, paper);
                }
            }
        }

        for (Map.Entry<Long, Set<Long>> entry : userToMatchedPaperIds.entrySet()) {
            int count = entry.getValue().size();
            User user = userMap.get(entry.getKey());

            Notification notification = Notification.builder()
                    .user(user)
                    .content("Có " + count + " bài báo mới thuộc các chủ đề/journal bạn đang theo dõi.")
                    .type("NEW_PAPER")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
        }
    }

    private void registerMatch(Map<Long, User> userMap, Map<Long, Set<Long>> userToPaperIds, Follow follow, ResearchPaper paper) {
        Long userId = follow.getUser().getId();
        userMap.put(userId, follow.getUser());
        userToPaperIds.computeIfAbsent(userId, k -> new HashSet<>()).add(paper.getId());
    }

    public PageResponse<NotificationResponse> listMine(Long userId, Pageable pageable) {
        Page<Notification> result = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(result.map(this::toResponse));
    }

    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông báo"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền với thông báo này");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getContent(), n.getType(), n.isRead(), n.getCreatedAt());
    }
}