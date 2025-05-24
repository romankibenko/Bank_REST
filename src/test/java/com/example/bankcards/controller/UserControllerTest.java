// UserControllerTest.java
package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.service.AdminUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserServiceImpl adminUserServiceImpl;

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockUser_ShouldReturnUserResponse() throws Exception {
        // Given
        UserResponse response = new UserResponse(1L, "user@test.com", UserRole.ROLE_USER, false);
        when(adminUserServiceImpl.blockUser(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/admin/users/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}