package com.example.bankcards.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.entity.Card;
import java.util.List;

@Service
public class DataMigrationService {

    private final CardRepository cardRepository;
    private final String encryptionSecret;
    private final String salt;

    public DataMigrationService(
            CardRepository cardRepository,
            @Value("${card.encryption.secret}") String encryptionSecret,
            @Value("${card.encryption.salt}") String salt
    ) {
        if (encryptionSecret == null || salt == null) {
            throw new IllegalStateException("Encryption secrets are not configured!");
        }
        this.cardRepository = cardRepository;
        this.encryptionSecret = encryptionSecret;
        this.salt = salt;
    }

    @Transactional
    public void encryptExistingCardNumbers() {
        if (encryptionSecret == null || salt == null) {
            throw new IllegalStateException("Encryption secrets not configured!");
        }
        TextEncryptor encryptor = Encryptors.text(encryptionSecret, salt);
        List<Card> cards = cardRepository.findAll();

        for (Card card : cards) {
            String originalNumber = card.getNumber();
            if (originalNumber == null || originalNumber.isBlank()) {
                continue;
            }
            String encryptedNumber = encryptor.encrypt(originalNumber);
            card.setNumber(encryptedNumber);
            cardRepository.save(card);
        }
    }
}