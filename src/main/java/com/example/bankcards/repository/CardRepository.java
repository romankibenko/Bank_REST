package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CardRepository extends
        JpaRepository<Card, Long>,
        JpaSpecificationExecutor<Card> {

    Optional<Card> findByIdAndUserId(Long cardId, Long userId);
    Optional<Card> findByNumberAndUserId(String number, Long userId);
    Optional<Card> findByNumber(String number);
    boolean existsByNumber(String encryptedNumber);
}
