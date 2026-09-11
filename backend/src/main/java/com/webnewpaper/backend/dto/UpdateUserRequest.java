package com.webnewpaper.backend.dto;

import com.webnewpaper.backend.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUserRequest {
    private Role role;
    private Boolean enabled;
}