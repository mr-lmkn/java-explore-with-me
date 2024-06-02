package ru.practicum.ewmServer.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.users.dto.NewUserRequestDto;
import ru.practicum.ewmServer.users.dto.UserDto;
import ru.practicum.ewmServer.users.service.UsersService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UsersAdminController {
    private final UsersService usersService;

    @GetMapping
    public List<UserDto> getAll(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size
    ) {
        log.info("getAll users request ids = {}, from = {}, size = {}", ids, from, size);
        return usersService.getAll(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Validated NewUserRequestDto newUser) {
        log.info("create user request {}", newUser);
        return usersService.create(newUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) throws NotFoundException {
        log.info("deleteUser request id = {}", id);
        usersService.delete(id);
    }
}
