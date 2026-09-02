package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.FollowRequest;
import com.webnewpaper.backend.dto.FollowResponse;
import com.webnewpaper.backend.dto.FollowStatusResponse;
import com.webnewpaper.backend.enums.TargetType;
import com.webnewpaper.backend.security.CurrentUser;
import com.webnewpaper.backend.services.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final CurrentUser currentUser;

    public FollowController(FollowService followService, CurrentUser currentUser) {
        this.followService = followService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<FollowResponse> follow(@RequestBody FollowRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(followService.follow(currentUser.getUserId(jwt), request.getTargetType(), request.getTargetId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unfollow(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        followService.unfollow(currentUser.getUserId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<FollowResponse>> myFollows(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(followService.listMine(currentUser.getUserId(jwt)));
    }

    @GetMapping("/check")
    public ResponseEntity<FollowStatusResponse> checkStatus(
            @RequestParam TargetType targetType,
            @RequestParam Long targetId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(followService.checkStatus(currentUser.getUserId(jwt), targetType, targetId));
    }
}