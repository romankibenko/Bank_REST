package com.example.bankcards.service;

import com.example.bankcards.config.CardNumberEncryptor;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.CardRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardNumberEncryptor cardNumberEncryptor;

    @Autowired
    public CardService(CardRepository cardRepository, CardNumberEncryptor cardNumberEncryptor) {
        this.cardRepository = cardRepository;
        this.cardNumberEncryptor = cardNumberEncryptor;
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getUserCards(Long userId, String status, Pageable pageable) {
        Specification<Card> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), CardStatus.valueOf(status)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return cardRepository.findAll(spec, pageable)
                .map(CardResponse::new);
    }

    @Transactional
    public void requestCardBlock(Long userId, Long cardId) {
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CardOperationException("Cannot block expired card");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Transactional
    public void transferBetweenUserCards(Long userId, TransferRequest request) {
        String encryptedFrom = cardNumberEncryptor.encrypt(request.fromCardNumber());
        String encryptedTo = cardNumberEncryptor.encrypt(request.toCardNumber());

        Card sourceCard = cardRepository.findByNumberAndUserId(encryptedFrom, userId)
                .orElseThrow(() -> new CardNotFoundException("Source card not found"));

        Card targetCard = cardRepository.findByNumberAndUserId(encryptedTo, userId)
                .orElseThrow(() -> new CardNotFoundException("Target card not found"));

        if (sourceCard.getBalance().compareTo(request.amount()) < 0) {
            throw new CardOperationException("Insufficient funds");
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(request.amount()));
        targetCard.setBalance(targetCard.getBalance().add(request.amount()));

        cardRepository.saveAll(List.of(sourceCard, targetCard));
    }

    @Transactional(readOnly = true)
    public BigDecimal getCardBalance(Long userId, Long cardId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .map(Card::getBalance)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
    }
}