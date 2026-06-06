package com.example.bankcards.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void cardCreateRequest_ShouldValidateNumberFormat() {
        CardCreateRequest invalidRequest = new CardCreateRequest(
                1L,
                "1234", // Invalid length
                LocalDate.now().plusYears(1)
        );

        assertEquals(1, validator.validate(invalidRequest).size());
    }

    @Test
    void transferRequest_ShouldValidatePositiveAmount() {
        TransferRequest invalidRequest = new TransferRequest(
                "1111",
                "2222",
                new BigDecimal("-100") // Negative amount
        );

        assertEquals(1, validator.validate(invalidRequest).size());
    }
}