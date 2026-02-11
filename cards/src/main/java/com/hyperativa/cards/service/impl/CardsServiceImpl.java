package com.hyperativa.cards.service.impl;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.entity.Cards;
import com.hyperativa.cards.entity.User;
import com.hyperativa.cards.exception.CardAlreadyExistsException;
import com.hyperativa.cards.exception.ResourceNotFoundException;
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
        Optional<User> optionalUser = userRepository.findByUserName(cardDto.getUser().getUserName());
        if(optionalUser.isEmpty()){
            throw  new CardAlreadyExistsException("User not found" +
                    cardDto.getUser().getUserName());
        }
        cardDto.getUser().setId(optionalUser.get().getId());
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

    /*
     * @param mobileNumber - Input Mobile Number
     * @return Card Details based on a given mobileNumber
     */
    @Override
    public CardDto fetchCard(String cardNumber) {
        Cards cards = cardsRepository.findByCardNumber(cardNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", cardNumber)
        );
        return CardsMapper.mapToCardsDto(cards, new CardDto());
    }


}
