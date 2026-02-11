package com.hyperativa.cards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "cards")
@Getter
@Setter
public class Cards extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "card_number", nullable = false, unique = true, length = 16)
    private String cardNumber;
}
