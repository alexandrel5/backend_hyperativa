package com.hyperativa.cards.service;

import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardLookupResponse;
import com.hyperativa.cards.dto.card.CardProcessLineDto;
import com.hyperativa.cards.entity.ApiLogEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface IApiLogService {

    void saveLog(ApiLogEntity log);
}
