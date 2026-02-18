package com.hyperativa.cards.service;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardLookupResponse;
import com.hyperativa.cards.dto.card.CardProcessLineDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface ICardsService {


    CardProcessLineDto createSingleCard(UUID ownerSub, CardDto cardDto);

    CardBatchResultDto processCardsFile(MultipartFile file, UUID ownerSub);

    CardDto fetchCard(String cardNumber);

    Optional<CardLookupResponse> lookupCard(String cardNumber);
}
