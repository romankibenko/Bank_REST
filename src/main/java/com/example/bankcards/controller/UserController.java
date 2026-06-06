package com.example.bankcards.controller;

import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.AdminUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    private final AdminUserServiceImpl adminUserServiceImpl;

    @Autowired
    public UserController(AdminUserServiceImpl adminUserServiceImpl) {
        this.adminUserServiceImpl = adminUserServiceImpl;
    }

    @PutMapping("/{userId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse blockUser(@PathVariable Long userId) {
        return adminUserServiceImpl.blockUser(userId);
    }

    @PutMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse activateUser(@PathVariable Long userId) {
        return adminUserServiceImpl.activateUser(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long userId) {
        adminUserServiceImpl.deleteUser(userId);
    }
}