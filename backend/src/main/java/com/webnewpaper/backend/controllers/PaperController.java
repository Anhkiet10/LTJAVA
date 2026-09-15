package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.PageResponse;
import com.webnewpaper.backend.dto.PaperDetailResponse;
import com.webnewpaper.backend.dto.PaperSummaryResponse;
import com.webnewpaper.backend.services.PaperService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PaperSummaryResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String journal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long keywordId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("publicationYear").descending());
        return ResponseEntity.ok(paperService.search(keyword, author, journal, year, keywordId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaperDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paperService.getById(id));
    }
}