package com.hyperativa.cards.service.impl;

import com.hyperativa.cards.constants.CardProcessingConstants;
import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardProcessLineDto;
import com.hyperativa.cards.entity.Cards;
import com.hyperativa.cards.entity.User;
import com.hyperativa.cards.exception.ResourceNotFoundException;
import com.hyperativa.cards.mapper.CardsMapper;
import com.hyperativa.cards.repository.ApiLogRepository;
import com.hyperativa.cards.repository.CardsRepository;
import com.hyperativa.cards.repository.UserRepository;
import com.hyperativa.cards.service.ICardsService;
import com.hyperativa.cards.service.fileupload.CardExtractionService;
import jakarta.transaction.Transactional;
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


    @Transactional
    public CardProcessLineDto createSingleCard(CardDto cardDto) {

        User user = getUserOrThrow(cardDto.getUser().getUserName());

        String cardNumber = normalizeCardNumber(cardDto.getCardNumber());

        if (cardNumber == null || cardNumber.isBlank()) {
            return new CardProcessLineDto(null,
                    CardProcessingConstants.STATUS_ERROR,
                    CardProcessingConstants.MSG_CARD_NUMBER_REQUIRED,
                    null);
        }

        List<String> singleList = List.of(cardNumber);

        CardBatchResultDto batchResult = processCardsInternal(singleList, user);

        return batchResult.details().get(0);
    }

    @Transactional
    public CardBatchResultDto processCardsFile(MultipartFile file, String username) {

        User user = getUserOrThrow(username);

        List<String> extractedCards;
        try {
            extractedCards = extractionService.extractAndValidateCards(file);
        } catch (IOException e) {

            return new CardBatchResultDto(
                    CardProcessingConstants.STATUS_FAILED,
                    CardProcessingConstants.MSG_CANNOT_READ_FILE,
                    List.of()
            );
        }

        if (extractedCards.isEmpty()) {
            return new CardBatchResultDto(
                    CardProcessingConstants.STATUS_FAILED,
                    CardProcessingConstants.MSG_NO_VALID_CARDS_IN_FILE,
                    List.of()
            );
        }

        CardBatchResultDto result = processCardsInternal(extractedCards, user);

        // You can enrich message with file-specific info if desired
        return result;
    }

    private CardBatchResultDto processCardsInternal(List<String> cardNumbers, User user) {

        // Deduplicate - keep first occurrence
        LinkedHashSet<String> uniqueSet = new LinkedHashSet<>(cardNumbers);
        List<String> toProcess = new ArrayList<>(uniqueSet);
        int duplicatesIgnored = cardNumbers.size() - toProcess.size();

        // Check existing in DB
        Set<String> numbersToCheck = new HashSet<>(toProcess);
        Set<String> alreadyExists = cardsRepository.findByCardNumberIn(numbersToCheck)
                .stream()
                .map(Cards::getCardNumber)
                .collect(Collectors.toSet());

        List<CardProcessLineDto> lines = new ArrayList<>();
        List<Cards> toSave = new ArrayList<>();

        int savedCount = 0;
        int alreadyExistsCount = 0;

        for (String cardNumber : toProcess) {

            if (alreadyExists.contains(cardNumber)) {
                lines.add(new CardProcessLineDto(
                        cardNumber,
                        CardProcessingConstants.STATUS_ALREADY_EXISTS,
                        CardProcessingConstants.MSG_ALREADY_REGISTERED,
                        null
                ));
                alreadyExistsCount++;
                continue;
            }

            Cards entity = new Cards();
            entity.setCardNumber(cardNumber);
            entity.setUser(user);
            entity.setCreatedAt(LocalDateTime.now());
            // entity.setMaskedNumber(...);
            // entity.setBrand(...);

            toSave.add(entity);
            lines.add(new CardProcessLineDto(
                    cardNumber,
                    CardProcessingConstants.STATUS_SUCCESS,
                    CardProcessingConstants.MSG_CARD_WILL_BE_CREATED,
                    null
            ));
        }

        if (!toSave.isEmpty()) {
            List<Cards> saved = cardsRepository.saveAll(toSave);
            savedCount = saved.size();

            // Optional: update lines with real IDs (if CardProcessLine is mutable or rebuild)
        }

        String status = savedCount == toProcess.size() ? "SUCCESS"
                : (savedCount > 0 ? "PARTIAL" : "FAILED");

        String message = String.format(
                "%d card(s) processed (%d unique), %d saved, %d already exist%s",
                cardNumbers.size(),
                toProcess.size(),
                savedCount,
                alreadyExistsCount,
                duplicatesIgnored > 0 ? ", " + duplicatesIgnored + " duplicate(s) ignored" : ""
        );

        return new CardBatchResultDto(
                status,
                message,
                lines
        );
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException(String.format(CardProcessingConstants.MSG_USER_NOT_FOUND, username)));
    }

//    private CardBatchResultDto failedBatch(String msg) {
//        return new CardBatchResultDto("FAILED", msg, List.of());
//    }

    private String normalizeCardNumber(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("\\D", ""); // remove spaces, dashes, etc.
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
