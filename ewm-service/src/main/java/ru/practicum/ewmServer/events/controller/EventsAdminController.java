package ru.practicum.ewmServer.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.config.constants.Constants;
import ru.practicum.ewmServer.dtoValidateGroups.GroupUpdate;
import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.dto.EventFullDto;
import ru.practicum.ewmServer.events.dto.UpdateEventAdminRequestDto;
import ru.practicum.ewmServer.events.dto.sesrch.EventAdminSearchRequest;
import ru.practicum.ewmServer.events.enums.EventStatus;
import ru.practicum.ewmServer.events.service.EventsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventsAdminController {
    private final EventsService eventsService;

    @GetMapping
    public List<EventFullDto> adminGetAll(
            @RequestParam(required = false, value = "users") List<Long> users,
            @RequestParam(required = false, value = "states") EventStatus states,
            @RequestParam(required = false, value = "categories") List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) throws BadRequestException {
        EventAdminSearchRequest filter = EventAdminSearchRequest.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        log.info("adminGetAll events request serch = {}", filter);
        return eventsService.adminGetAll(filter);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto adminUpdate(
            @PathVariable(value = "eventId") @Positive Long eventId,
            @RequestBody @Validated(GroupUpdate.class) UpdateEventAdminRequestDto newEventDto
    ) throws ConflictException, NotFoundException, BadRequestException {
        log.info("adminUpdate request eventId = {}, inputUpdate = {}", eventId, newEventDto);
        return eventsService.adminUpdate(eventId, newEventDto);
    }
}
