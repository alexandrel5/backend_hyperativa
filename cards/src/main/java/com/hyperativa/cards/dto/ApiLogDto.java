package com.hyperativa.cards.dto;

import lombok.Data;

@Data
public class ApiLogDto {

    private String action;

    private String requestData;

    private String responseData;
}
