package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(
        Long id,
        String maskedNumber,
        String holderName,
        LocalDate expiryDate,
        CardStatus status,
        BigDecimal balance
) {
    public CardResponse(Card card) {
        this(
                card.getId(),
                maskNumber(card.getNumber()),
                card.getHolderName(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }

    private static String maskNumber(String encryptedNumber) {
        return "**** **** **** " + encryptedNumber.substring(encryptedNumber.length() - 4);
    }

}