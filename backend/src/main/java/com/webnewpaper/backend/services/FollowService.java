package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.FollowResponse;
import com.webnewpaper.backend.dto.FollowStatusResponse;
import com.webnewpaper.backend.entity.Follow;
import com.webnewpaper.backend.entity.Journal;
import com.webnewpaper.backend.entity.Keyword;
import com.webnewpaper.backend.entity.User;
import com.webnewpaper.backend.enums.TargetType;
import com.webnewpaper.backend.repositories.FollowRepository;
import com.webnewpaper.backend.repositories.JournalRepository;
import com.webnewpaper.backend.repositories.KeywordRepository;
import com.webnewpaper.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final JournalRepository journalRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository,
                          KeywordRepository keywordRepository, JournalRepository journalRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.keywordRepository = keywordRepository;
        this.journalRepository = journalRepository;
    }

    public FollowResponse follow(Long userId, TargetType targetType, Long targetId) {
        if (followRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            throw new IllegalArgumentException("Bạn đã theo dõi mục này rồi");
        }

        String targetName = resolveTargetName(targetType, targetId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        Follow follow = Follow.builder()
                .user(user)
                .targetType(targetType)
                .targetId(targetId)
                .build();

        follow = followRepository.save(follow);
        return new FollowResponse(follow.getId(), targetType, targetId, targetName);
    }

    public void unfollow(Long userId, Long followId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy theo dõi này"));

        if (!follow.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy theo dõi này");
        }

        followRepository.delete(follow);
    }

    public List<FollowResponse> listMine(Long userId) {
        return followRepository.findByUserId(userId).stream()
                .map(f -> new FollowResponse(f.getId(), f.getTargetType(), f.getTargetId(),
                        resolveTargetName(f.getTargetType(), f.getTargetId())))
                .collect(Collectors.toList());
    }

    public FollowStatusResponse checkStatus(Long userId, TargetType targetType, Long targetId) {
        return followRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)
                .map(f -> new FollowStatusResponse(true, f.getId()))
                .orElse(new FollowStatusResponse(false, null));
    }

    private String resolveTargetName(TargetType targetType, Long targetId) {
        if (targetType == TargetType.KEYWORD) {
            Keyword keyword = keywordRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy keyword"));
            return keyword.getName();
        } else {
            Journal journal = journalRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy journal"));
            return journal.getName();
        }
    }
}