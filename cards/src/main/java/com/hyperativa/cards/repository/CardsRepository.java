package com.hyperativa.cards.repository;

import com.hyperativa.cards.entity.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CardsRepository extends JpaRepository<Cards, UUID> {
    boolean existsByCardHash(String cardHash);

    @Query("SELECT c.id FROM Cards c WHERE c.cardHash = :hash")
    Optional<UUID> findIdByCardHash(@Param("hash") String cardHash);

    List<Cards> findByCardHashIn(Collection<String> hashes);

    // Optional helpers
    Optional<Cards> findByCardHash(String cardHash);
}
