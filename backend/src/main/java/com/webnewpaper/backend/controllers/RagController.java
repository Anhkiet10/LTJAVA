package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.AskRequest;
import com.webnewpaper.backend.dto.AskResponse;
import com.webnewpaper.backend.services.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/api/ask")
    public ResponseEntity<?> ask(@RequestBody AskRequest request) {
        try {
            AskResponse response = ragService.ask(request.getQuestion(), request.getPaperId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }
}