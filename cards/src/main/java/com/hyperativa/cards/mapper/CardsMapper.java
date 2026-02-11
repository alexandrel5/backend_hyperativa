package com.hyperativa.cards.mapper;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.entity.Cards;

public class CardsMapper {
    public static CardDto mapToCardsDto(Cards cards, CardDto cardDto){
        cardDto.setUser(cards.getUser());
        cardDto.setCardNumber(cards.getCardNumber());
        return cardDto;
    }

    public static Cards mapToCards(CardDto cardDto, Cards cards){
        cards.setId(cardDto.getUser().getId());
        cards.setUser(cardDto.getUser());
        cards.setCardNumber(cardDto.getCardNumber());
        return cards;
    }

}
