package com.hyperativa.cards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Table(name = "cards")
@Getter
@Setter
public class Cards extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_hash", nullable = false, length = 64, unique = true)
    private String cardHash;          // SHA-256 hex → 64 chars

    @Column(name = "last_four", length = 4, nullable = false)
    private String lastFour;
}
