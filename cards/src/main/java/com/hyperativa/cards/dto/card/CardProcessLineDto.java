package com.hyperativa.cards.dto.card;

public record CardProcessLineDto(
        String cardNumber,
        String status,          // SUCCESS | ALREADY_EXISTS | INVALID_LUHN | ERROR
        String message,
        Long savedId            // null if not saved
) {}
