package com.hyperativa.cards.dto.card;

public record CardLookupResponse(
        boolean exists,
        Long systemId,       // null if not found
        String message       // optional
) {}