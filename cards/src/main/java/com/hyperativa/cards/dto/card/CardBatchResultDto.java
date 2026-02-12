package com.hyperativa.cards.dto.card;

import java.util.List;

public record CardBatchResultDto(
        String status,
        String message,
        List<CardProcessLineDto> details
) {}

