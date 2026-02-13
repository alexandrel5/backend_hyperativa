package com.hyperativa.cards.dto.card;

import java.util.UUID;

public record CardProcessLineDto(
        String cardNumber,
        String status,          // SUCCESS | ALREADY_EXISTS | INVALID_LUHN | ERROR
        String message,
        UUID savedId            // null if not saved
) {}
