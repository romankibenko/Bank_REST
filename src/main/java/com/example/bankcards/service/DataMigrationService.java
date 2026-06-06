package com.example.bankcards.service;

import com.example.bankcards.config.CardNumberEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.entity.Card;
import java.util.List;

@Service
public class DataMigrationService {

    /** Открытый номер карты — ровно 16 цифр. Зашифрованный (queryableText) выглядит иначе. */
    private static final String PLAINTEXT_NUMBER_REGEX = "\\d{16}";

    private final CardRepository cardRepository;
    private final CardNumberEncryptor cardNumberEncryptor;

    public DataMigrationService(CardRepository cardRepository,
                                CardNumberEncryptor cardNumberEncryptor) {
        this.cardRepository = cardRepository;
        this.cardNumberEncryptor = cardNumberEncryptor;
    }

    @Transactional
    public void encryptExistingCardNumbers() {
        List<Card> cards = cardRepository.findAll();

        for (Card card : cards) {
            String originalNumber = card.getNumber();
            // Идемпотентность: шифруем только ещё не зашифрованные (открытые) номера,
            // иначе повторный запуск приложения зашифровал бы данные второй раз.
            if (originalNumber == null || !originalNumber.matches(PLAINTEXT_NUMBER_REGEX)) {
                continue;
            }
            card.setLastFourDigits(originalNumber.substring(originalNumber.length() - 4));
            card.setNumber(cardNumberEncryptor.encrypt(originalNumber));
            cardRepository.save(card);
        }
    }
}
