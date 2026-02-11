package com.hyperativa.cards.controller;

import com.hyperativa.cards.constants.CardsConstants;
import com.hyperativa.cards.dto.CardDto;
import com.hyperativa.cards.dto.ResponseDto;
import com.hyperativa.cards.service.ICardsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
public class CardsController {

    private ICardsService iCardsService;

    @PostMapping("/card")
    public ResponseEntity<ResponseDto> createCard(@RequestBody CardDto cardDto){

    iCardsService.createCard(cardDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));

    }
}
