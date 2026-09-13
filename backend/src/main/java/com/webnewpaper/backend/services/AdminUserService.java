package com.webnewpaper.backend.services;

import com.webnewpaper.backend.dto.AdminUserResponse;
import com.webnewpaper.backend.dto.UpdateUserRequest;
import com.webnewpaper.backend.entity.User;
import com.webnewpaper.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminUserResponse> listAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AdminUserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        return toResponse(userRepository.save(user));
    }

    private AdminUserResponse toResponse(User user) {
        return new AdminUserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole(), user.isEnabled(), user.getCreatedAt());
    }
}