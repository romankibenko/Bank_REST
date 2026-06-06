package com.example.bankcards.service;

import com.example.bankcards.config.CardNumberEncryptor;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataMigrationServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardNumberEncryptor cardNumberEncryptor;

    @InjectMocks
    private DataMigrationService dataMigrationService;

    @Test
    void encryptExistingCardNumbers_ShouldEncryptPlaintextCardAndSetLastFour() {
        // Given: карта с открытым 16-значным номером
        Card card = new Card();
        card.setNumber("4111111111111111");
        when(cardRepository.findAll()).thenReturn(List.of(card));
        when(cardNumberEncryptor.encrypt("4111111111111111")).thenReturn("encrypted");

        // When
        dataMigrationService.encryptExistingCardNumbers();

        // Then
        verify(cardRepository, times(1)).save(card);
        assertEquals("encrypted", card.getNumber());
        assertEquals("1111", card.getLastFourDigits());
    }

    @Test
    void encryptExistingCardNumbers_ShouldSkipAlreadyEncryptedCard() {
        // Given: номер уже зашифрован (не похож на 16 цифр) -> повторно не шифруем
        Card card = new Card();
        card.setNumber("U2FsdGVkX1+already+encrypted==");
        when(cardRepository.findAll()).thenReturn(List.of(card));

        // When
        dataMigrationService.encryptExistingCardNumbers();

        // Then
        verify(cardRepository, never()).save(any());
        verify(cardNumberEncryptor, never()).encrypt(any());
    }
}
