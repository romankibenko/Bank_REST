package com.example.bankcards.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Единая точка шифрования номеров карт.
 *
 * Использует детерминированное AES/CBC (фиксированный ключ и IV, выведенные из
 * secret и salt). Детерминированность обязательна: один и тот же номер всегда
 * даёт одинаковый шифротекст, иначе поиск карты по номеру (переводы) был бы
 * невозможен — пришлось бы расшифровывать всю таблицу.
 */
@Component
public class CardNumberEncryptor {

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec key;
    private final IvParameterSpec iv;

    public CardNumberEncryptor(
            @Value("${card.encryption.secret}") String secret,
            @Value("${card.encryption.salt}") String salt
    ) {
        if (secret == null || salt == null) {
            throw new IllegalStateException("Card encryption secrets are not configured!");
        }
        byte[] keyBytes = sha256(secret);                          // 32 байта -> AES-256
        byte[] ivBytes = Arrays.copyOf(sha256(salt), 16);         // 16 байт -> IV
        this.key = new SecretKeySpec(keyBytes, "AES");
        this.iv = new IvParameterSpec(ivBytes);
    }

    public String encrypt(String plainNumber) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encrypted = cipher.doFinal(plainNumber.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt card number", e);
        }
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
