package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.AdminCardService;
import com.example.bankcards.service.AdminUserService;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CardService cardService;

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private AdminCardService adminCardService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void handleCardNotFoundException_ShouldReturnNotFound() throws Exception {
        CardController controller = new CardController(cardService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        UserPrincipal principal = mock(UserPrincipal.class);
        lenient().when(principal.getId()).thenReturn(1L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));

        when(cardService.getCardBalance(any(), any()))
                .thenThrow(new CardNotFoundException("Card not found"));

        mockMvc.perform(get("/api/cards/999/balance"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Card not found"));
    }

    @Test
    void handleUserAlreadyExistsException_ShouldReturnBadRequest() throws Exception {
        AdminController controller = new AdminController(adminUserService, adminCardService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        UserCreateRequest request = new UserCreateRequest("exists@test.com", "pass", UserRole.ROLE_USER);
        when(adminUserService.createUser(any()))
                .thenThrow(new UserAlreadyExistsException("exists@test.com"));

        mockMvc.perform(post("/api/admin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User 'exists@test.com' already exists"));
    }

    @Test
    void handleDisabledException_ShouldReturnUnauthorized() throws Exception {
        AuthController controller = new AuthController(
                authenticationManager, jwtTokenProvider, userRepository, passwordEncoder);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        AuthRequest request = new AuthRequest("disabled@test.com", "pass");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("User is disabled"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User is disabled"));
    }
}
