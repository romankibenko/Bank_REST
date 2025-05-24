package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AdminCardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminCardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        Long userId = request.userId();
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(userId));

        Card card = new Card();
        card.setNumber(generateCardNumber());
        card.setHolderName(user.getHolderName());
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setUser(user);

        return new CardResponse(cardRepository.save(card));
    }

    private String generateCardNumber() {
        return "4" + String.format("%015d", (long) (Math.random() * 1_000_000_000_000L));
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(CardResponse::new);
    }

    @Transactional
    public CardResponse blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));
        card.setStatus(CardStatus.BLOCKED);
        return new CardResponse(cardRepository.save(card));
    }
}