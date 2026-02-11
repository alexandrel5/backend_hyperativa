package com.hyperativa.cards.dto;

import lombok.Data;

@Data
public class UserDto {
    private String username;

    private String passwordHash;
}
