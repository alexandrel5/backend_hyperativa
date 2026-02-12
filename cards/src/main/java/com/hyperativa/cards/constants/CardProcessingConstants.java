package com.hyperativa.cards.constants;

import java.util.Set;

public final class CardProcessingConstants {

    private CardProcessingConstants() {
    }

    public static final String UPLOAD_ENDPOINT = "/api/upload/text";
    public static final String FORM_FILE_PARAM_NAME = "file";

    public static final long MAX_FILE_SIZE_BYTES = 2 * 1024 * 1024; // 2 MB
    public static final Set<String> ALLOWED_EXTENSIONS = Set.of("txt");
    public static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "text/plain",
            "application/octet-stream"   // sometimes used when sending plain .txt
    );

    // Error messages
    public static final String ERR_FILE_REQUIRED = "File is required";
    public static final String ERR_FILE_EMPTY = "Uploaded file is empty";
    public static final String ERR_FILE_TOO_LARGE = "File size exceeds 2MB limit";
    public static final String ERR_INVALID_CONTENT_TYPE = "Only text/plain files are allowed";
    public static final String ERR_INVALID_EXTENSION = "Only .txt files are allowed";
    public static final String ERR_INVALID_FILENAME = "Filename contains invalid or dangerous characters";
    public static final String ERR_SUCCESS = "File received successfully: ";

    // Characters often used in path traversal, command injection, etc.
    public static final String DANGEROUS_FILENAME_CHARS = "..;/\\?%*:|\"<>";


    // ───────────────────────────────────────────────
    // Status values
    // ───────────────────────────────────────────────
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_PARTIAL = "PARTIAL";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_ALREADY_EXISTS = "ALREADY_EXISTS";
    public static final String STATUS_ERROR = "ERROR";

    // ───────────────────────────────────────────────
    // Common messages & message patterns
    // ───────────────────────────────────────────────
    public static final String MSG_USER_NOT_FOUND = "User not found: %s";
    public static final String MSG_NO_VALID_CARDS = "No valid card numbers found";
    public static final String MSG_NO_VALID_CARDS_IN_FILE = "No valid card numbers found in file";
    public static final String MSG_CANNOT_READ_FILE = "Cannot read file: %s";
    public static final String MSG_CARD_NUMBER_REQUIRED = "Card number is required";

    public static final String MSG_ALREADY_REGISTERED = "Card already registered";
    public static final String MSG_CARD_WILL_BE_CREATED = "Card will be created";
    public static final String MSG_CARD_QUEUED_FOR_CREATION = "Card queued for creation";

    // Summary message templates
    public static final String TEMPLATE_SUMMARY_SINGLE_OR_BATCH =
            "%d card(s) processed (%d unique), %d saved, %d already exist%s";

    public static final String TEMPLATE_SUMMARY_WITH_DUPLICATES =
            "%d cards processed (%d unique), %d saved, %d already exist, %d duplicate(s) ignored";

    // ───────────────────────────────────────────────
    // Other fixed values / magic strings
    // ───────────────────────────────────────────────
    public static final String INTERNAL_ERROR_NO_RESULT = "Internal error: no result generated";

    // If you normalize card numbers in a specific way
    // (you can keep the regex here if it's always the same)
    public static final String CARD_NUMBER_NON_DIGIT_REGEX = "\\D";

    // If you ever decide to limit processed cards per request/file
    // (optional - comment out if not needed now)
    // public static final int MAX_CARDS_PER_BATCH = 500;
}