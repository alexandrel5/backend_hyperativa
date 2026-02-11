package com.hyperativa.cards.repository;

import com.hyperativa.cards.entity.Cards;
import com.hyperativa.cards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardsRepository extends JpaRepository<Cards, User> {
    Optional<Cards> findByCardNumber(String cardNumber);
}
