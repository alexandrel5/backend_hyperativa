package com.hyperativa.cards.controller;

import com.hyperativa.cards.constants.CardProcessingConstants;
import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardLookupResponse;
import com.hyperativa.cards.dto.card.CardProcessLineDto;
import com.hyperativa.cards.exception.CardAlreadyExistsException;
import com.hyperativa.cards.service.ICardsService;
import com.hyperativa.cards.service.fileupload.CardExtractionService;
import com.hyperativa.cards.service.fileupload.FileUploadValidationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/card/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class CardsController {

    private static final Logger log = LoggerFactory.getLogger(CardsController.class);

    private ICardsService iCardsService;
    private final FileUploadValidationService validationService;
    private final CardExtractionService extractionService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> createCard(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CardDto cardDto) {           // ← add @Valid if using validation

        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid authentication");
        }

        try {
            // Extract Keycloak user identifier
            UUID ownerSub = UUID.fromString(jwt.getSubject());  // "52637dff-25e0-411e-acf4-ccc23fc748d9"

            // Optional: you can also read other claims if needed
            // String username = jwt.getClaimAsString("preferred_username");
            // String email    = jwt.getClaimAsString("email");

            // Pass the owner_sub + DTO to service
            CardProcessLineDto result = iCardsService.createSingleCard(ownerSub, cardDto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(result);

        } catch (Exception e) {
            log.error("Card batch failed", e);
            return ResponseEntity.status(422)
                    .body(new CardBatchResultDto("ERROR", e.getMessage(), List.of()));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndExtractCards(
            @RequestParam("file")
            @NotNull(message = CardProcessingConstants.ERR_FILE_REQUIRED)
            MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        if (jwt == null || !StringUtils.hasText(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CardBatchResultDto(
                            CardProcessingConstants.STATUS_ERROR,
                            "Authentication required - valid JWT token needed",
                            List.of()
                    ));
        }

        UUID ownerSub;
        try {
            ownerSub = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid subject format in JWT", e);
            return ResponseEntity.badRequest()
                    .body(new CardBatchResultDto(
                            CardProcessingConstants.STATUS_ERROR,
                            "Invalid user identifier in token",
                            List.of()
                    ));
        }

        // 2. File validation (keep your existing logic)
        var validation = validationService.validate(file);
        if (!validation.isValid()) {
            return ResponseEntity.badRequest().body(validation.message());
        }

        String filename = validation.filename();
        log.info("Processing file upload for user sub: {} - file: {}", ownerSub, filename);

        try {
            // 3. Call service with ownerSub instead of username
            CardBatchResultDto result = iCardsService.processCardsFile(file, ownerSub);

            // Optional: log success to api_logs if you want
            // apiLogService.logAction(ownerSub, "UPLOAD_CARDS_FILE", filename, "SUCCESS", ...);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Card batch processing failed for user sub: {}", ownerSub, e);

            //apiLogService.logAction(ownerSub, "UPLOAD_CARDS_FILE", filename, "ERROR", e.getMessage());

            return ResponseEntity.status(422)
                    .body(new CardBatchResultDto(
                            CardProcessingConstants.STATUS_ERROR,
                            "Failed to process file: " + e.getMessage(),
                            List.of()
                    ));
        }
    }

    @PostMapping("/lookup")
    public ResponseEntity<CardLookupResponse> lookupCardPost(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody Map<String, String> payload) {

        System.out.println(Map.of("sub", jwt.getSubject(), "username", jwt.getClaimAsString("preferred_username"), "email", jwt.getClaimAsString("email"), "roles", jwt.getClaim("realm_access")));

        String cardNumber = payload.get("cardNumber");
        return ResponseEntity.ok(
                iCardsService.lookupCard(cardNumber)
                        .orElse(new CardLookupResponse(false, null, "Invalid or missing card number")));
    }

}
