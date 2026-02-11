package com.hyperativa.cards.service.impl;

import com.hyperativa.cards.dto.UserDto;
import com.hyperativa.cards.entity.User;
import com.hyperativa.cards.exception.CardAlreadyExistsException;
import com.hyperativa.cards.mapper.UserMapper;
import com.hyperativa.cards.repository.ApiLogRepository;
import com.hyperativa.cards.repository.CardsRepository;
import com.hyperativa.cards.repository.UserRepository;
import com.hyperativa.cards.service.IUsersService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements IUsersService {

    private CardsRepository cardsRepository;
    private UserRepository userRepository;
    private ApiLogRepository apiLogRepository;


    @Override
    public void createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto, new User());
        Optional<User> optionalUser = userRepository.findByUserName(userDto.getUserName());
        if (optionalUser.isPresent()) {
            throw new CardAlreadyExistsException("User already registered with given name"
                    + userDto.getUserName());
        }
        user.setPasswordHash(userDto.getPasswordHash());
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
    }
}
