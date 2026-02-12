package com.hyperativa.cards.constants;

import java.util.Set;

public final class FileUploadConstants {

    private FileUploadConstants() {
        // utility class - prevent instantiation
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
}