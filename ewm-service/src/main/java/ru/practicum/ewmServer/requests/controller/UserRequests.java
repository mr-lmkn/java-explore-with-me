package ru.practicum.ewmServer.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmServer.requests.service.RequestsService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserRequests {
    private final RequestsService requestsService;

    @PostMapping(value = "/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequestFromUser(
            @PathVariable(value = "userId") @Positive Long userId,
            @RequestParam(name = "eventId") @Positive Long eventId
    ) throws NotFoundException, ConflictException {
        log.info("createParticipant request userId = {}, eventId = {}", userId, eventId);
        return requestsService.createRequestFromUser(userId, eventId);
    }

    @GetMapping(value = "/{userId}/requests")
    public List<ParticipationRequestDto> getAllByUserIdRequestFromUser(
            @PathVariable(value = "userId") @Positive Long userId
    ) throws NotFoundException {
        log.info("getAllParticipants request userId = {}", userId);
        return requestsService.getAllByUserIdRequestFromUser(userId);
    }

    @PatchMapping(value = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto setCancelRequestFromUser(
            @PathVariable(value = "userId") @Positive Long userId,
            @PathVariable(value = "requestId") @Positive Long requestId
    ) throws NotFoundException, ConflictException {
        log.info("updateParticipant, set cancel Request userId= {} , requestId = {}", userId, requestId);
        return requestsService.setCancelRequestFromUser(userId, requestId);
    }
}
