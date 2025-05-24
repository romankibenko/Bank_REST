package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Source card number is required")
        String fromCardNumber,

        @NotBlank(message = "Target card number is required")
        String toCardNumber,

        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {}