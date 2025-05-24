package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminCardService adminCardService;

    @Test
    void createCard_ShouldReturnCardResponse() {
        // Given
        LocalDate expiryDate = LocalDate.now().plusYears(3);
        String cardNumber = "4111111111111111";
        CardCreateRequest request = new CardCreateRequest(1L, cardNumber, expiryDate);

        User user = new User();
        user.setId(1L);
        user.setHolderName("John Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card card = inv.getArgument(0);
            card.setId(1L);
            card.setNumber(cardNumber);
            card.setHolderName(user.getHolderName());
            card.setStatus(CardStatus.ACTIVE);
            card.setBalance(BigDecimal.valueOf(0.0));
            return card;
        });

        // When
        CardResponse response = adminCardService.createCard(request);

        // Then
        assertNotNull(response);
        assertEquals("**** **** **** 1111", response.maskedNumber());
        assertEquals("John Doe", response.holderName());
        assertEquals(expiryDate, response.expiryDate());
        assertEquals(CardStatus.ACTIVE, response.status());
        assertEquals(0.0, response.balance().doubleValue());
        verify(cardRepository, times(1)).save(any());
    }

    @Test
    void blockCard_ShouldThrowException_WhenCardNotFound() {
        // Given
        Long invalidCardId = 999L;
        when(cardRepository.findById(invalidCardId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class,
                () -> adminCardService.blockCard(invalidCardId)
        );
    }
}