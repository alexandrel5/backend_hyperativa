package com.hyperativa.cards.service.impl;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.CardProcessResultDto;
import com.hyperativa.cards.dto.fileupload.CardBatchResultDto;
import com.hyperativa.cards.dto.fileupload.CardProcessLine;
import com.hyperativa.cards.entity.Cards;
import com.hyperativa.cards.entity.User;
import com.hyperativa.cards.exception.ResourceNotFoundException;
import com.hyperativa.cards.mapper.CardsMapper;
import com.hyperativa.cards.repository.ApiLogRepository;
import com.hyperativa.cards.repository.CardsRepository;
import com.hyperativa.cards.repository.UserRepository;
import com.hyperativa.cards.service.ICardsService;
import com.hyperativa.cards.service.fileupload.CardExtractionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CardsServiceImpl implements ICardsService {

    private CardsRepository cardsRepository;
    private UserRepository userRepository;
    private ApiLogRepository apiLogRepository;
    private CardExtractionService extractionService;

    private static final Logger log = LoggerFactory.getLogger(CardsServiceImpl.class);


//    @Override
//    public void createCard(CardDto cardsDto) {
//        List<CardDto> cardList = new ArrayList<>();
//        cardList.add(cardsDto);
//        createCardList(cardList);
//    }
//
//    @Override
//    public void createCardList(List<CardDto> cardsDto) {
//        for (var cardDto : cardsDto){
//            Optional<User> optionalUser = userRepository.findByUserName(cardDto.getUser().getUserName());
//            if(optionalUser.isEmpty()){
//                throw  new CardAlreadyExistsException("User not found" +
//                        cardDto.getUser().getUserName());
//            }
//            cardDto.getUser().setId(optionalUser.get().getId());
//
//
//            Optional<Cards> optionalCards = cardsRepository.findByCardNumber(cardDto.getCardNumber());
//            if(optionalCards.isPresent()){
//                throw  new CardAlreadyExistsException("Card already registered with given number"
//                        +cardDto.getCardNumber());
//            }
//
//            Cards savedCard = cardsRepository.save(createNewCard(cardDto));
//            //apiLogRepository.save()
//        }
//
//    }

    @Override
    public void createCard(CardDto cardDto) {

    }

    @Override
    public CardBatchResultDto processCardsFile(MultipartFile file, String username) {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<String> allExtractedCards;
        try {
            allExtractedCards = extractionService.extractAndValidateCards(file);
        } catch (IOException e) {
            return new CardBatchResultDto("FAILED", "Cannot read file: " + e.getMessage(), List.of());
        }

        if (allExtractedCards.isEmpty()) {
            return new CardBatchResultDto("FAILED", "No valid card numbers found", List.of());
        }

        // ───────────────────────────────────────────────
        //   DEDUPLICATE HERE – keep first occurrence
        // ───────────────────────────────────────────────
        LinkedHashSet<String> uniqueCardsSet = new LinkedHashSet<>(allExtractedCards);
        List<String> cardsToProcess = new ArrayList<>(uniqueCardsSet);

        int duplicatesRemoved = allExtractedCards.size() - cardsToProcess.size();

        // ───────────────────────────────────────────────
        // Now work only with unique card numbers
        // ───────────────────────────────────────────────

        // Bulk check which ones already exist in DB
        Set<String> cardNumbersForQuery = new HashSet<>(cardsToProcess);
        List<Cards> existing = cardsRepository.findByCardNumberIn(cardNumbersForQuery);
        Set<String> alreadyExists = existing.stream()
                .map(Cards::getCardNumber)
                .collect(Collectors.toSet());

        List<CardProcessLine> results = new ArrayList<>();
        List<Cards> toSave = new ArrayList<>();

        int savedCount = 0;
        int skippedCount = 0;

        for (String cardNumber : cardsToProcess) {

            if (alreadyExists.contains(cardNumber)) {
                results.add(new CardProcessLine(
                        cardNumber,
                        "ALREADY_EXISTS",
                        "Card already registered in the system",
                        null
                ));
                skippedCount++;
                continue;
            }

            Cards entity = new Cards();
            entity.setCardNumber(cardNumber);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUser(user);

            toSave.add(entity);

            results.add(new CardProcessLine(
                    cardNumber,
                    "SUCCESS",
                    "Card queued for creation",
                    null
            ));
        }

        // Bulk save
        if (!toSave.isEmpty()) {
            List<Cards> savedEntities = cardsRepository.saveAll(toSave);
            savedCount = savedEntities.size();

            // Optional: match saved IDs back to results
            Map<String, Long> idByCardNumber = savedEntities.stream()
                    .collect(Collectors.toMap(Cards::getCardNumber, Cards::getId));

            // You can update results here if CardProcessLine allows mutation
            // or rebuild the list – depends on whether your DTO is mutable
        }

        String status = savedCount == cardsToProcess.size() ? "SUCCESS"
                : (savedCount > 0 ? "PARTIAL" : "FAILED");

        String summary = String.format(
                "%d cards processed (%d unique), %d saved, %d already exist, %d duplicates ignored",
                allExtractedCards.size(),
                cardsToProcess.size(),
                savedCount,
                skippedCount,
                duplicatesRemoved
        );

        return new CardBatchResultDto(status, summary, results);
    }

    private Cards createNewCard(CardDto cardDto) {
        Cards newCard = new Cards();
        newCard.setCardNumber(cardDto.getCardNumber());
        newCard.setCreatedAt(LocalDateTime.now());
        newCard.setUser(cardDto.getUser());

        return newCard;
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
