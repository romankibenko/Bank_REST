package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CardCreateRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "\\d{16}", message = "Invalid card number")
        String number,

        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate
) {}