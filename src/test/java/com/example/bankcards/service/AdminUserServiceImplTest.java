package com.example.bankcards.service;

import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    @Test
    void activateUser_ShouldActivateInactiveUser() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // When
        UserResponse response = adminUserService.activateUser(userId);

        // Then
        assertTrue(response.active());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser_ShouldThrowExceptionIfUserNotFound() {
        // Given
        Long invalidUserId = 999L;
        when(userRepository.existsById(invalidUserId)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class,
                () -> adminUserService.deleteUser(invalidUserId)
        );
    }

    @Test
    void getAllUsers_ShouldReturnPagedResults() {
        // Given
        User user = new User();
        user.setUsername("test@example.com");
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<UserResponse> result = adminUserService.getAllUsers(Pageable.unpaged());

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("test@example.com", result.getContent().get(0).username());
    }
}