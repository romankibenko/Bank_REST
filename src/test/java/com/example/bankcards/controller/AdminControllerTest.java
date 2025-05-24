package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.service.AdminCardService;
import com.example.bankcards.service.AdminUserService;
import com.example.bankcards.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, CustomUserDetailsService.class})
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter; // Добавлено!

    @MockBean
    private AdminCardService adminCardService;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private AdminController adminController;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_ShouldReturnCreated() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest("admin@test.com", "password", UserRole.ROLE_ADMIN);
        UserResponse response = new UserResponse(1L, "admin@test.com", UserRole.ROLE_ADMIN, true);

        when(userRepository.existsByUsername("admin@test.com")).thenReturn(false); // Замокать проверку
        when(adminUserService.createUser(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnPage() throws Exception {
        // Given
        UserResponse user = new UserResponse(1L, "user@test.com", UserRole.ROLE_ADMIN, true);
        Page<UserResponse> page = new PageImpl<>(List.of(user));
        when(adminUserService.getAllUsers(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("user@test.com"));
    }
}