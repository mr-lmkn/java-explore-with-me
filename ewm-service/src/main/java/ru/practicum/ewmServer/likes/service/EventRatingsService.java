package ru.practicum.ewmServer.likes.service;

import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.likes.dto.EventTopChartDto;
import ru.practicum.ewmServer.likes.dto.RateDto;

import java.util.List;

public interface EventRatingsService {
    RateDto rateEvent(Long eventId, Long userId, Boolean isLike) throws NotFoundException, BadRequestException, ConflictException;

    void deleteRateEvent(Long eventId, Long userId) throws NotFoundException;

    List<EventTopChartDto> getEventsTopChart(
            List<Long> eventIds, List<Long> userIds,
            Boolean onlyAvailable, Boolean onlyFuture,
            Integer from, Integer size);

}
