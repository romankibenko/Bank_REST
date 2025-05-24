// CardControllerTest.java
package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import(SecurityConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @Test
    @WithMockUser(roles = "USER")
    void getUserCards_ShouldReturnPage() throws Exception {
        // Given
        CardResponse card = new CardResponse(1L, "**** **** **** 1111", "User",
                LocalDate.now().plusYears(3), CardStatus.ACTIVE, new BigDecimal("1000.00"));
        Page<CardResponse> page = new PageImpl<>(List.of(card));
        when(cardService.getUserCards(any(), any(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value("**** **** **** 1111"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void transferBetweenCards_ShouldReturnOk() throws Exception {
        // Given
        TransferRequest request = new TransferRequest("1111", "2222", new BigDecimal("100.00"));
        doNothing().when(cardService).transferBetweenUserCards(any(), any());

        // When & Then
        mockMvc.perform(post("/api/cards/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}