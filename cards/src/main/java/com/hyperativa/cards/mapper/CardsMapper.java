package com.hyperativa.cards.mapper;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.entity.Cards;

public class CardsMapper {
    public static CardDto mapToCardsDto(Cards cards, CardDto cardDto){
        cardDto.setUser(cards.getUser());
        cardDto.setCardNumber(cards.getCardHash());
        return cardDto;
    }

    public static Cards mapToCards(CardDto cardDto, Cards cards){
        cards.setUser(cardDto.getUser());
        cards.setCardHash(cardDto.getCardNumber());
        return cards;
    }

}
