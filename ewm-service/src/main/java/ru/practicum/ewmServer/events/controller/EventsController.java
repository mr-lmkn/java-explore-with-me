package ru.practicum.ewmServer.events.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.config.constants.Constants;
import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.dto.EventFullDto;
import ru.practicum.ewmServer.events.dto.EventShortDto;
import ru.practicum.ewmServer.events.dto.sesrch.EventSearchRequest;
import ru.practicum.ewmServer.events.enums.EventsSort;
import ru.practicum.ewmServer.events.service.EventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventsController {
    private final EventsService eventsService;

    @GetMapping
    public List<EventShortDto> publicGetFilteredEvents(
            @RequestParam(required = false) @Min(3) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size,
            HttpServletRequest request) throws BadRequestException {
        EventSearchRequest searchRequest = EventSearchRequest.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(EventsSort.valueOf(sort))
                .from(from)
                .size(size)
                .build();
        log.info("publicGetFilteredEvents request {}", searchRequest);

        return eventsService.publicGetFilteredEvents(searchRequest, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable(value = "eventId") @Positive Long eventId,
                                     HttpServletRequest request)
            throws NotFoundException, JsonProcessingException {
        log.info("getEventById id= {}", eventId);
        return eventsService.publicGetById(eventId, request);
    }
}
