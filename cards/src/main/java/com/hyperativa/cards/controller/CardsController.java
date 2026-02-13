package com.hyperativa.cards.controller;

import com.hyperativa.cards.constants.CardProcessingConstants;
import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.card.CardBatchResultDto;
import com.hyperativa.cards.dto.card.CardLookupResponse;
import com.hyperativa.cards.dto.card.CardProcessLineDto;
import com.hyperativa.cards.service.ICardsService;
import com.hyperativa.cards.service.fileupload.CardExtractionService;
import com.hyperativa.cards.service.fileupload.FileUploadValidationService;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/card/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class CardsController {

    private static final Logger log = LoggerFactory.getLogger(CardsController.class);

    private ICardsService iCardsService;
    private final FileUploadValidationService validationService;
    private final CardExtractionService extractionService;

    @PostMapping("/create")
    public ResponseEntity<?> createCard(@RequestBody CardDto cardDto) {

        try {
            CardProcessLineDto result = iCardsService.createSingleCard(cardDto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Card batch failed", e);
            return ResponseEntity.status(422)
                    .body(new CardBatchResultDto("ERROR", e.getMessage(), List.of()));
        }
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(new ResponseDto(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));

    }

    @GetMapping("/fetch")
    public ResponseEntity<CardDto> fetchCardDetails(@RequestParam
                                                    @Pattern(regexp = "(^$|[0-9]{10})", message = "Card number must be 10 digits")
                                                    String mobileNumber) {
        CardDto cardsDto = iCardsService.fetchCard(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(cardsDto);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndExtractCards(
            @RequestParam("file")
            @NotNull(message = CardProcessingConstants.ERR_FILE_REQUIRED)
            MultipartFile file,
            @RequestParam(value = "username", required = false) String formUsername) {

        var validation = validationService.validate(file);
        if (!validation.isValid()) {
            return ResponseEntity.badRequest().body(validation.message());
        }
        String targetUsername;

        if (StringUtils.hasText(formUsername)) {
            targetUsername = formUsername;
        } else {
            return ResponseEntity.badRequest()
                    .body(new CardBatchResultDto("ERROR", "User not identified", List.of()));
        }

        String filename = validation.filename();
        log.info("Processing file: {}", filename);

        try {
            CardBatchResultDto result = iCardsService.processCardsFile(file, targetUsername);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Card batch failed", e);
            return ResponseEntity.status(422)
                    .body(new CardBatchResultDto("ERROR", e.getMessage(), List.of()));
        }

    }

    @PostMapping("/lookup")
    public ResponseEntity<CardLookupResponse> lookupCardPost(
            @RequestBody Map<String, String> payload) {

        String cardNumber = payload.get("cardNumber");
        return ResponseEntity.ok(
                iCardsService.lookupCard(cardNumber)
                        .orElse(new CardLookupResponse(false, null, "Invalid or missing card number")));
    }

}
