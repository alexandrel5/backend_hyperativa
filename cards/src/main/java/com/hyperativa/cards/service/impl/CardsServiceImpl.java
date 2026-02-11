package com.hyperativa.cards.service.impl;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.entity.Cards;
import com.hyperativa.cards.exception.CardAlreadyExistsException;
import com.hyperativa.cards.mapper.CardsMapper;
import com.hyperativa.cards.repository.ApiLogRepository;
import com.hyperativa.cards.repository.CardsRepository;
import com.hyperativa.cards.repository.UserRepository;
import com.hyperativa.cards.service.ICardsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CardsServiceImpl implements ICardsService {

    private CardsRepository cardsRepository;
    private UserRepository userRepository;
    private ApiLogRepository apiLogRepository;

    @Override
    public void createCard(CardDto cardDto) {
        Cards cards = CardsMapper.mapToCards(cardDto, new Cards());
        Optional<Cards> optionalCards = cardsRepository.findByCardNumber(cardDto.getCardNumber());
        if(optionalCards.isPresent()){
            throw  new CardAlreadyExistsException("Card already registered with given number"
                    +cardDto.getCardNumber());
        }
        cards.setCreatedAt(LocalDateTime.now());
        Cards savedCard = cardsRepository.save(cards);
        //apiLogRepository.save()
    }
}
