package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.AdminUserResponse;
import com.webnewpaper.backend.dto.UpdateUserRequest;
import com.webnewpaper.backend.services.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> list() {
        return ResponseEntity.ok(adminUserService.listAll());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminUserResponse> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(adminUserService.update(id, request));
    }
}