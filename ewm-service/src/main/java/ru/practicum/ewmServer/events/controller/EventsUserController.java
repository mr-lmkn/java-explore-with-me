package ru.practicum.ewmServer.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.dtoValidateGroups.GroupCreate;
import ru.practicum.ewmServer.dtoValidateGroups.GroupUpdate;
import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.dto.EventFullDto;
import ru.practicum.ewmServer.events.dto.EventShortDto;
import ru.practicum.ewmServer.events.dto.NewEventDto;
import ru.practicum.ewmServer.events.dto.UpdateEventUserRequestDto;
import ru.practicum.ewmServer.events.service.EventsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class EventsUserController {
    private final EventsService eventsService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto userCreateEvent(
            @PathVariable(value = "userId") @Positive Long userId,
            @RequestBody @Validated(GroupCreate.class) NewEventDto newEventDto
    ) throws NotFoundException, ConflictException, BadRequestException {
        log.info("userCreateEvent request userId = {}, input = {}", userId, newEventDto);
        if (Objects.isNull(newEventDto.getRequestModeration())) {
            newEventDto.setRequestModeration(Boolean.TRUE);
        }
        if (Objects.isNull(newEventDto.getParticipantLimit())) {
            newEventDto.setParticipantLimit(0);
        }
        if (Objects.isNull(newEventDto.getParticipantLimit())) {
            newEventDto.setParticipantLimit(0);
        }
        if (Objects.isNull(newEventDto.getPaid())) {
            newEventDto.setPaid(Boolean.FALSE);
        }
        return eventsService.userCreateEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllEventsByUserId(
            @PathVariable(value = "userId") @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size
    ) throws NotFoundException {
        log.info("getAllEventsByUserIdPage getAllByUserId request id= {}", userId);
        return eventsService.getAllEventsByUserIdPage(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto userGetEventById(
            @PathVariable(value = "userId") @Positive Long userId,
            @PathVariable(value = "eventId") @Positive Long eventId
    ) throws NotFoundException {
        log.info("userGetEventById request userId = {}, eventId = {}", userId, eventId);
        return eventsService.userGetById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto userUpdateEvent(
            @PathVariable(value = "userId") @Positive Long userId,
            @PathVariable(value = "eventId") @Positive Long eventId,
            @RequestBody @Validated(GroupUpdate.class) UpdateEventUserRequestDto newEventDto
    ) throws ConflictException, NotFoundException, BadRequestException {
        log.info("userUpdateEvent request userId = {}, eventId = {}, inputUpdate = {}", userId, eventId, newEventDto);
        return eventsService.userUpdate(userId, eventId, newEventDto);
    }

}
