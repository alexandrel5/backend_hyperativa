package com.hyperativa.cards.service.impl;

import com.hyperativa.cards.constants.CardProcessingConstants;
import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardLookupResponse;
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
import com.hyperativa.cards.util.CardHashUtil;
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

        if (cardNumbers == null || cardNumbers.isEmpty()) {
            return new CardBatchResultDto(
                    CardProcessingConstants.STATUS_FAILED,
                    "No card numbers provided",
                    List.of()
            );
        }

        // 1. Normalize & deduplicate (keep first occurrence order)
        List<String> toProcess = cardNumbers.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()                     // or keep LinkedHashSet if order matters
                .toList();

        int duplicatesIgnored = cardNumbers.size() - toProcess.size();

        // 2. Compute hashes for cards we will check
        Map<String, String> hashToOriginal = new LinkedHashMap<>(); // preserve order
        Set<String> cardHashes = new HashSet<>();

        for (String pan : toProcess) {
            String hash = CardHashUtil.hashCardNumber(pan);
            hashToOriginal.put(hash, pan);   // first occurrence wins
            cardHashes.add(hash);
        }
        // 3. Bulk check which hashes already exist in database
        List<Cards> existing = cardsRepository.findByCardHashIn(cardHashes);
        Set<String> existingHashes = existing.stream()
                .map(Cards::getCardHash)
                .collect(Collectors.toSet());

        // 4. Prepare results and entities to save
        List<CardProcessLineDto> lines = new ArrayList<>();
        List<Cards> toSave = new ArrayList<>();

        int savedCount = 0;
        int alreadyExistsCount = 0;

        for (String hash : hashToOriginal.keySet()) {   // ordered by first appearance
            String originalPan = hashToOriginal.get(hash);

            if (existingHashes.contains(hash)) {
                lines.add(new CardProcessLineDto(
                        originalPan,                            // show original or masked
                        CardProcessingConstants.STATUS_ALREADY_EXISTS,
                        CardProcessingConstants.MSG_ALREADY_REGISTERED,
                        null
                ));
                alreadyExistsCount++;
                continue;
            }

            // New card → prepare entity
            Cards entity = new Cards();
            entity.setCardHash(hash);
            entity.setLastFour(CardHashUtil.extractLastFour(originalPan));
            entity.setUser(user);

            toSave.add(entity);

        }

        if (!toSave.isEmpty()) {
            List<Cards> saved = cardsRepository.saveAll(toSave);
            savedCount = saved.size();

            for (var card : saved) {
                lines.add(new CardProcessLineDto(
                        card.getCardHash(),
                        CardProcessingConstants.STATUS_SUCCESS,
                        CardProcessingConstants.MSG_CARD_CREATED_SUCCESSFULLY,
                        card.getId()
                ));
            }
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
        String hash = CardHashUtil.hashCardNumber(cardNumber);
        Cards cards = cardsRepository.findByCardHash(cardNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "cardNumber", cardNumber)
        );
        return CardsMapper.mapToCardsDto(cards, new CardDto());
    }


    private Optional<UUID> getCardSystemIdByNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isBlank()) {
            return Optional.empty();
        }
        String hash = CardHashUtil.hashCardNumber(cardNumber);
        return cardsRepository.findIdByCardHash(hash);
    }

    @Override
    public Optional<CardLookupResponse> lookupCard(String cardNumber) {
        return getCardSystemIdByNumber(cardNumber)
                .map(id -> new CardLookupResponse(true, id, null))
                .or(() -> Optional.of(new CardLookupResponse(false, null, "Card not found")));
    }


}
