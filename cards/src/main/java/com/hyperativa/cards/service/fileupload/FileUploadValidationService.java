package com.hyperativa.cards.service.fileupload;

import com.hyperativa.cards.constants.CardProcessingConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Service
public class FileUploadValidationService {

    public ValidationResult validate(MultipartFile file) {
        if (file == null) {
            return ValidationResult.invalid(CardProcessingConstants.ERR_FILE_REQUIRED);
        }

        // 1. Not empty
        if (file.isEmpty()) {
            return ValidationResult.invalid(CardProcessingConstants.ERR_FILE_EMPTY);
        }

        // 2. Size limit
        if (file.getSize() > CardProcessingConstants.MAX_FILE_SIZE_BYTES) {
            return ValidationResult.invalid(CardProcessingConstants.ERR_FILE_TOO_LARGE);
        }

        // 3. Content-Type
        String contentType = file.getContentType();
        if (contentType == null ||
                !CardProcessingConstants.ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            return ValidationResult.invalid(CardProcessingConstants.ERR_INVALID_CONTENT_TYPE);
        }

        // 4. Extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return ValidationResult.invalid(CardProcessingConstants.ERR_INVALID_FILENAME);
        }

        String extension = getExtension(originalFilename).toLowerCase(Locale.ROOT);
        if (!CardProcessingConstants.ALLOWED_EXTENSIONS.contains(extension)) {
            return ValidationResult.invalid(CardProcessingConstants.ERR_INVALID_EXTENSION);
        }

        return ValidationResult.valid(originalFilename);
    }

    private String getExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    private boolean containsDangerousCharacters(String filename) {
        for (char c : CardProcessingConstants.DANGEROUS_FILENAME_CHARS.toCharArray()) {
            if (filename.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    // Small record (Java 14+) or class to carry result
    public record ValidationResult(boolean isValid, String message, String filename) {

        public static ValidationResult valid(String filename) {
            return new ValidationResult(true, null, filename);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message, null);
        }
    }
}
