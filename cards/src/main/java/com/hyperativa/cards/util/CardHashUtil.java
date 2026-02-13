package com.hyperativa.cards.util;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public class CardHashUtil {

    public static String hashCardNumber(String pan) {
        if (pan == null || pan.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be empty");
        }

        // Normalize: remove spaces, dashes, etc.
        String normalized = pan.replaceAll("\\D", "");

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes); // 64 hex chars
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash card number", e);
        }
    }

    // Optional: quick last4 + brand detection
    public static String extractLastFour(String pan) {
        String digits = pan.replaceAll("\\D", "");
        return digits.length() >= 4 ? digits.substring(digits.length() - 4) : "";
    }
}