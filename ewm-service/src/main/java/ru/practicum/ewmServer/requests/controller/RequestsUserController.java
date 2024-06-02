package ru.practicum.ewmServer.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmServer.requests.dto.statusUpdate.RequestStatusUpdateRequestDto;
import ru.practicum.ewmServer.requests.dto.statusUpdate.RequestStatusUpdateResponseDto;
import ru.practicum.ewmServer.requests.service.RequestsService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class RequestsUserController {
    private final RequestsService requestsService;

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllParticipationRequestAsInitiator(
            @PathVariable(value = "userId") @Positive Long userId,
            @PathVariable(value = "eventId") @Positive Long eventId
    ) throws NotFoundException {
        log.info("Event getAllParticipationRequestAsInitiator request = {}", userId);
        return requestsService.getAllParticipationRequestAsInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResponseDto updateStatusRequestFromInitiator(
            @PathVariable(value = "userId") @Positive Long userId,
            @PathVariable(value = "eventId") @Positive Long eventId,
            @RequestBody @Validated RequestStatusUpdateRequestDto inputUpdate
    ) throws ConflictException, NotFoundException {
        log.info("Event updateStatusRequestFromInitiator userId = {}, eventId = {}, inputUpdate = {}", userId, eventId, inputUpdate);
        return requestsService.updateStatusRequestFromInitiator(userId, eventId, inputUpdate);
    }
}
