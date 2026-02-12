package com.hyperativa.cards.service.fileupload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CardExtractionService {

    public List<String> extractAndValidateCards(MultipartFile file) throws IOException {
        List<String> validCards = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || !trimmed.startsWith("C")) {
                    continue;
                }

                // Try position-based extraction first (most reliable for fixed format)
                String candidate = extractFromFixedPosition(line);

                // Fallback: regex style
                if (candidate == null || candidate.isBlank()) {
                    candidate = extractWithRegex(trimmed);
                }

                //if (candidate != null && !candidate.isBlank() && LuhnValidator.isValid(candidate)) {
                if (candidate != null && !candidate.isBlank()) {
                    validCards.add(candidate);
                }
            }
        }

        return validCards;
    }

    private String extractFromFixedPosition(String line) {
        // Card number usually starts at column 8 (0-based index 7), length ~19
        if (line.length() < 8) {
            return null;
        }
        // Take from position 7 → 26 (19 chars), then trim
        int start = 7;
        int end = Math.min(line.length(), 26);
        String part = line.substring(start, end).trim();

        return part.matches("\\d+") ? part : null;
    }

    private String extractWithRegex(String line) {
        // Cxx + spaces + digits
        String[] parts = line.split("\\s+");
        if (parts.length >= 2) {
            String last = parts[parts.length - 1].trim();
            if (last.matches("\\d+")) {
                return last;
            }
        }
        return null;
    }
}