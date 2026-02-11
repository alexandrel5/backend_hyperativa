package com.hyperativa.cards.dto;

import com.hyperativa.cards.entity.User;
import lombok.Data;

@Data
public class ApiLogDto {
    private User user;

    private String action;

    private String requestData;

    private String responseData;
}
