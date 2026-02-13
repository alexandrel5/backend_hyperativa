package com.hyperativa.cards.dto.card;

import java.util.UUID;

public record CardLookupResponse(
        boolean exists,
        UUID systemId,       // null if not found
        String message       // optional
) {}