package com.hyperativa.cards.service;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardProcessLineDto;
import org.springframework.web.multipart.MultipartFile;

public interface ICardsService {


    CardProcessLineDto createSingleCard(CardDto cardDto);

    CardBatchResultDto processCardsFile(MultipartFile file, String username);

    CardDto fetchCard(String cardNumber);
}
