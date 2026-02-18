package com.hyperativa.cards.service;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardLookupResponse;
import com.hyperativa.cards.dto.card.CardProcessLineDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface ICardsService {


    CardProcessLineDto createSingleCard(CardDto cardDto, UUID ownerSub);

    CardBatchResultDto processCardsFile(MultipartFile file, UUID ownerSub);

    CardDto fetchCard(String cardNumber);

    Optional<CardLookupResponse> lookupCard(CardDto cardDto, UUID ownerSub);
}
