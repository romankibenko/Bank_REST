package com.example.bankcards.dto;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;

public record UserResponse(
        Long id,
        String username,
        UserRole role,
        boolean active
) {
    public UserResponse(User user) {
        this(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isActive()
        );
    }
}