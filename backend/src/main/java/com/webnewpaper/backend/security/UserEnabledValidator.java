package com.webnewpaper.backend.security;

import com.webnewpaper.backend.repositories.UserRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class UserEnabledValidator implements OAuth2TokenValidator<Jwt> {

    private final UserRepository userRepository;

    public UserEnabledValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        Object rawUserId = token.getClaim("userId");
        if (rawUserId == null) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Thiếu userId trong token", null));
        }

        Long userId = ((Number) rawUserId).longValue();
        boolean enabled = userRepository.findById(userId)
                .map(com.webnewpaper.backend.entity.User::isEnabled)
                .orElse(false);

        if (!enabled) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Tài khoản đã bị khóa", null));
        }

        return OAuth2TokenValidatorResult.success();
    }
}