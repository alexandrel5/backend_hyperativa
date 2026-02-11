package com.hyperativa.cards.controller;

import com.hyperativa.cards.constants.CardsConstants;
import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.ResponseDto;
import com.hyperativa.cards.service.ICardsService;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/card/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
public class CardsController {

    private ICardsService iCardsService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(@RequestBody CardDto cardDto){

    iCardsService.createCard(cardDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));

    }

    @GetMapping("/fetch")
    public ResponseEntity<CardDto> fetchCardDetails(@RequestParam
                                                     @Pattern(regexp="(^$|[0-9]{10})",message = "Card number must be 10 digits")
                                                     String mobileNumber) {
        CardDto cardsDto = iCardsService.fetchCard(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(cardsDto);
    }
}
