package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.BookmarkResponse;
import com.webnewpaper.backend.dto.BookmarkStatusResponse;
import com.webnewpaper.backend.entity.Bookmark;
import com.webnewpaper.backend.entity.ResearchPaper;
import com.webnewpaper.backend.entity.User;
import com.webnewpaper.backend.repositories.BookmarkRepository;
import com.webnewpaper.backend.repositories.ResearchPaperRepository;
import com.webnewpaper.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ResearchPaperRepository paperRepository;
    private final UserRepository userRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository, ResearchPaperRepository paperRepository,
                            UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.paperRepository = paperRepository;
        this.userRepository = userRepository;
    }

    public BookmarkResponse addBookmark(Long userId, Long paperId) {
        if (bookmarkRepository.existsByUserIdAndPaperId(userId, paperId)) {
            throw new IllegalArgumentException("Bài báo này đã có trong danh sách bookmark của bạn");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        ResearchPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài báo"));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .paper(paper)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(bookmarkRepository.save(bookmark));
    }

    public void removeBookmark(Long userId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bookmark"));

        if (!bookmark.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền xóa bookmark này");
        }

        bookmarkRepository.delete(bookmark);
    }

    public List<BookmarkResponse> listMine(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BookmarkStatusResponse checkStatus(Long userId, Long paperId) {
        return bookmarkRepository.findByUserIdAndPaperId(userId, paperId)
                .map(b -> new BookmarkStatusResponse(true, b.getId()))
                .orElse(new BookmarkStatusResponse(false, null));
    }

    private BookmarkResponse toResponse(Bookmark bookmark) {
        return new BookmarkResponse(
                bookmark.getId(),
                bookmark.getPaper().getId(),
                bookmark.getPaper().getTitle(),
                bookmark.getCreatedAt()
        );
    }
}