package com.hyperativa.cards.service;

import com.hyperativa.cards.dto.CardDto;

public interface ICardsService {


    void createCard(CardDto cardDto);

    CardDto fetchCard(String cardNumber);
}
