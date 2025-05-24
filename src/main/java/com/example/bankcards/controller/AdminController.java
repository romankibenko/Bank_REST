package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.service.AdminUserService;
import com.example.bankcards.service.AdminCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminUserService userService;
    private final AdminCardService cardService;

    @Autowired
    public AdminController(
            AdminUserService userService,
            AdminCardService cardService
    ) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @GetMapping("/users")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping("/cards")
    public ResponseEntity<CardResponse> createCard(@RequestBody CardCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.createCard(request));
    }

    @PutMapping("/cards/{cardId}/block")
    public CardResponse blockCard(@PathVariable Long cardId) {
        return cardService.blockCard(cardId);
    }
}