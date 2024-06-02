package ru.practicum.ewmServer.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.dto.*;
import ru.practicum.ewmServer.events.dto.sesrch.EventAdminSearchRequest;
import ru.practicum.ewmServer.events.dto.sesrch.EventSearchRequest;
import ru.practicum.ewmServer.events.model.EventModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventsService {
    EventModel getEvent(Long id) throws NotFoundException;

    List<EventFullDto> adminGetAll(EventAdminSearchRequest filter) throws BadRequestException;

    EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequestDto newEventDto) throws NotFoundException, ConflictException;

    List<EventShortDto> publicGetFilteredEvents(
            EventSearchRequest eventSearchRequest,
            HttpServletRequest request
    ) throws BadRequestException;

    EventFullDto publicGetById(Long eventId, HttpServletRequest request) throws NotFoundException, JsonProcessingException;

    EventFullDto userCreateEvent(Long userId, NewEventDto event) throws NotFoundException, ConflictException;

    EventFullDto userUpdate(Long userId, Long eventId, UpdateEventUserRequestDto newEventDto) throws ConflictException, NotFoundException;

    List<EventShortDto> getAllEventsByUserIdPage(Long userId, Integer from, Integer size) throws NotFoundException;

    EventFullDto userGetById(Long userId, Long eventId) throws NotFoundException;
}
