package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<CardResponse> getUserCards(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String status
    ) {
        return cardService.getUserCards(user.getId(), status, pageable);
    }

    @PutMapping("/{cardId}/block-request")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestCardBlock(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long cardId
    ) {
        cardService.requestCardBlock(user.getId(), cardId);
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void transferBetweenCards(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid TransferRequest request
    ) {
        cardService.transferBetweenUserCards(user.getId(), request);
    }

    @GetMapping("/{cardId}/balance")
    @PreAuthorize("hasRole('USER')")
    public BigDecimal getCardBalance(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long cardId
    ) {
        return cardService.getCardBalance(user.getId(), cardId);
    }
}