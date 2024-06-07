package ru.practicum.ewmServer.likes.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.likes.dto.EventTopChartDto;
import ru.practicum.ewmServer.likes.dto.RateDto;
import ru.practicum.ewmServer.likes.service.EventRatingsService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventLikesController {
    private final EventRatingsService eventRatingsService;

    @PostMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public RateDto rateEvent(
            @PathVariable(value = "eventId") @Positive Long eventId,
            @RequestParam(value = "userId") @Positive Long userId,
            @RequestParam(value = "isLike") @NotNull Boolean isLike
    ) throws NotFoundException, ConflictException, BadRequestException {
        log.info("rateEvent request eventId = {}, userId = {}, isLike = {}", eventId, userId, isLike);
        return eventRatingsService.rateEvent(eventId, userId, isLike);
    }

    @PatchMapping("/{eventId}/like")
    public RateDto userUpdateEvent(
            @PathVariable(value = "eventId") @Positive Long eventId,
            @RequestParam(value = "userId") @Positive Long userId,
            @RequestParam(value = "isLike") @NotNull Boolean isLike
    ) throws ConflictException, NotFoundException, BadRequestException {
        log.info("reteEvent request eventId = {}, userId = {}, isLike = {}", eventId, userId, isLike);
        return eventRatingsService.rateEvent(eventId, userId, isLike);
    }

    @DeleteMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRateEvent(
            @PathVariable(value = "eventId") @Positive Long eventId,
            @RequestParam(value = "userId") @Positive Long userId
    ) throws NotFoundException, ConflictException, BadRequestException {
        log.info("deleteRateEvent request eventId = {}, userId = {}", eventId, userId);
        eventRatingsService.deleteRateEvent(eventId, userId);
    }

    @GetMapping("/top")
    public List<EventTopChartDto> getEventsTopChart(
            @RequestParam(value = "eventIds", required = false) @PositiveOrZero List<Long> eventIds,
            @RequestParam(value = "userIds", required = false) @PositiveOrZero List<Long> userIds,
            @RequestParam(value = "onlyAvailable", defaultValue = "false", required = false) @NotNull boolean onlyAvailable,
            @RequestParam(value = "onlyFuture", defaultValue = "false", required = false) @NotNull boolean onlyFuture,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size
    ) {
        if (Objects.isNull(eventIds)) eventIds = List.of(0L);
        if (Objects.isNull(userIds)) userIds = List.of(0L);

        log.info("getEventsTopChart request eventIds {}, userIds {}, onlyAvailable {}, onlyFuture {}, from {}, size {}",
                eventIds, userIds, onlyAvailable, onlyFuture, from, size);
        return eventRatingsService.getEventsTopChart(eventIds, userIds, onlyAvailable, onlyFuture, from, size);

    }

}

