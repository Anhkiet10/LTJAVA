package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.BookmarkRequest;
import com.webnewpaper.backend.dto.BookmarkResponse;
import com.webnewpaper.backend.dto.BookmarkStatusResponse;
import com.webnewpaper.backend.services.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.webnewpaper.backend.security.CurrentUser;
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final CurrentUser currentUser;

    public BookmarkController(BookmarkService bookmarkService, CurrentUser currentUser) {
        this.bookmarkService = bookmarkService;
        this.currentUser = currentUser;
    }

    @PostMapping
    public ResponseEntity<BookmarkResponse> add(@RequestBody BookmarkRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bookmarkService.addBookmark(currentUser.getUserId(jwt), request.getPaperId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        bookmarkService.removeBookmark(currentUser.getUserId(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookmarkResponse>> myBookmarks(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bookmarkService.listMine(currentUser.getUserId(jwt)));
    }

    @GetMapping("/check/{paperId}")
    public ResponseEntity<BookmarkStatusResponse> checkStatus(@PathVariable Long paperId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(bookmarkService.checkStatus(currentUser.getUserId(jwt), paperId));
    }
}