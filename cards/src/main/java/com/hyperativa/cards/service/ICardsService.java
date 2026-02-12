package com.hyperativa.cards.service;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.fileupload.CardBatchResultDto;
import org.springframework.web.multipart.MultipartFile;

public interface ICardsService {


    void createCard(CardDto cardDto);

    CardBatchResultDto processCardsFile(MultipartFile file, String username);

    CardDto fetchCard(String cardNumber);
}
