package ru.practicum.ewmServer.users.service;

import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.users.dto.NewUserRequestDto;
import ru.practicum.ewmServer.users.dto.UserDto;
import ru.practicum.ewmServer.users.model.UserModel;

import java.util.List;

public interface UsersService {
    List<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    UserDto create(NewUserRequestDto newUser);

    void delete(Long id) throws NotFoundException;

    UserModel getUser(Long id) throws NotFoundException;
}
