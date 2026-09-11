package com.webnewpaper.backend.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Long getUserId(Jwt jwt) {
        Object raw = jwt.getClaim("userId");
        return ((Number) raw).longValue();
    }

    public String getUsername(Jwt jwt) {
        return jwt.getSubject();
    }

    public String getRole(Jwt jwt) {
        return jwt.getClaim("role");
    }
}