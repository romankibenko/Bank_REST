package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void transferBetweenUserCards_ShouldUpdateBalances() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Card sourceCard = new Card();
        sourceCard.setNumber("4111111111111111");
        sourceCard.setBalance(new BigDecimal("1000.00"));
        sourceCard.setStatus(CardStatus.ACTIVE);
        sourceCard.setUser(user);

        Card targetCard = new Card();
        targetCard.setNumber("4222222222222222");
        targetCard.setBalance(new BigDecimal("500.00"));
        targetCard.setUser(user);

        TransferRequest request = new TransferRequest(
                "4111111111111111",
                "4222222222222222",
                new BigDecimal("300.00")
        );

        when(cardRepository.findByNumberAndUserId(request.fromCardNumber(), userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByNumber(request.toCardNumber()))
                .thenReturn(Optional.of(targetCard));

        // When
        cardService.transferBetweenUserCards(userId, request);

        // Then
        assertEquals(new BigDecimal("700.00"), sourceCard.getBalance());
        assertEquals(new BigDecimal("800.00"), targetCard.getBalance());
        verify(cardRepository, times(1)).saveAll(List.of(sourceCard, targetCard));
    }

    @Test
    void transferBetweenUserCards_ShouldThrowCardNotFoundException_WhenSourceCardNotFound() {
        // Given
        Long userId = 1L;
        TransferRequest request = new TransferRequest(
                "invalid_number",
                "4222222222222222",
                new BigDecimal("100.00")
        );

        when(cardRepository.findByNumberAndUserId(request.fromCardNumber(), userId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(CardNotFoundException.class, () ->
                cardService.transferBetweenUserCards(userId, request)
        );
    }

    @Test
    void transferBetweenUserCards_ShouldThrowCardOperationException_WhenInsufficientBalance() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Card sourceCard = new Card();
        sourceCard.setNumber("4111111111111111");
        sourceCard.setBalance(new BigDecimal("100.00"));
        sourceCard.setUser(user);

        Card targetCard = new Card();
        targetCard.setNumber("4222222222222222");
        targetCard.setUser(user);

        TransferRequest request = new TransferRequest(
                "4111111111111111",
                "4222222222222222",
                new BigDecimal("200.00")
        );

        when(cardRepository.findByNumberAndUserId(request.fromCardNumber(), userId))
                .thenReturn(Optional.of(sourceCard));
        when(cardRepository.findByNumber(request.toCardNumber()))
                .thenReturn(Optional.of(targetCard));

        // When & Then
        assertThrows(CardOperationException.class, () ->
                cardService.transferBetweenUserCards(userId, request)
        );
    }
}