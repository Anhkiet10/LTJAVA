package com.webnewpaper.backend.controllers;

import com.webnewpaper.backend.dto.AuthResponse;
import com.webnewpaper.backend.dto.LoginRequest;
import com.webnewpaper.backend.dto.RegisterRequest;
import com.webnewpaper.backend.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication authentication) {
    return ResponseEntity.ok("Xin chào " + authentication.getName() +
            ", quyền của bạn: " + authentication.getAuthorities());
}
}