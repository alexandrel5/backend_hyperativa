package com.hyperativa.cards.dto;

import com.hyperativa.cards.entity.User;
import lombok.Data;

@Data
public class CardDto {
    private User user;

    private String cardNumber;
}
