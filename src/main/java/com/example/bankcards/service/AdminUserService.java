package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AdminUserService {
    UserResponse createUser(UserCreateRequest request) throws UserAlreadyExistsException;

    UserResponse blockUser(Long userId) throws UserNotFoundException;

    UserResponse activateUser(Long userId) throws UserNotFoundException;

    void deleteUser(Long userId) throws UserNotFoundException;

    Page<UserResponse> getAllUsers(Pageable pageable);
}