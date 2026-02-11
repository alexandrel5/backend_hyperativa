package com.hyperativa.cards.mapper;

import com.hyperativa.cards.dto.UserDto;
import com.hyperativa.cards.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user, UserDto userDto){
        userDto.setUserName(user.getUserName());
        userDto.setPasswordHash(user.getPasswordHash());
        return userDto;
    }

    public static User mapToUser(UserDto userDto, User user){
        user.setUserName(userDto.getUserName());
        user.setPasswordHash(userDto.getPasswordHash());
        return user;
    }

}
