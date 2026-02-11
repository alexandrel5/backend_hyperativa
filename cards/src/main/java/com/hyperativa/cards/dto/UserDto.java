package com.hyperativa.cards.dto;

import lombok.Data;

@Data
public class UserDto {
    private String userName;

    private String passwordHash;
}
