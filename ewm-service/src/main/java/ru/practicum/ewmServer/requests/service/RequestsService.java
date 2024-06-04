package ru.practicum.ewmServer.requests.service;

import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmServer.requests.dto.statusUpdate.RequestStatusUpdateRequestDto;
import ru.practicum.ewmServer.requests.dto.statusUpdate.RequestStatusUpdateResponseDto;
import ru.practicum.ewmServer.requests.model.ParticipationRequestModel;

import java.util.List;

public interface RequestsService {
    ParticipationRequestDto createRequestFromUser(Long userId, Long eventId) throws NotFoundException, ConflictException;

    List<ParticipationRequestDto> getAllByUserIdRequestFromUser(Long userId) throws NotFoundException;

    ParticipationRequestDto setCancelRequestFromUser(Long userId, Long requestId) throws NotFoundException, ConflictException;

    List<ParticipationRequestDto> getAllParticipationRequestAsInitiator(Long userId, Long eventId) throws NotFoundException;

    RequestStatusUpdateResponseDto updateStatusRequestFromInitiator(Long userId, Long eventId, RequestStatusUpdateRequestDto toUpdate) throws ConflictException, NotFoundException;

    ParticipationRequestModel getUserRequest(Long id, Long userId) throws NotFoundException;

}
