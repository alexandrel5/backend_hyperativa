package com.hyperativa.cards.mapper;

import com.hyperativa.cards.dto.UserDto;
import com.hyperativa.cards.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user, UserDto userDto){
        userDto.setUsername(user.getUsername());
        userDto.setPasswordHash(user.getPasswordHash());
        return userDto;
    }

    public static User mapToUser(UserDto userDto, User user){
        user.setUsername(userDto.getUsername());
        user.setUsername(userDto.getPasswordHash());
        return user;
    }

}
