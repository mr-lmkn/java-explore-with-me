package ru.practicum.ewmServer.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.enums.EventStatus;
import ru.practicum.ewmServer.events.model.EventModel;
import ru.practicum.ewmServer.events.storage.EventsRepository;
import ru.practicum.ewmServer.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmServer.requests.dto.statusUpdate.RequestStatusUpdateRequestDto;
import ru.practicum.ewmServer.requests.dto.statusUpdate.RequestStatusUpdateResponseDto;
import ru.practicum.ewmServer.requests.emums.RequestStatus;
import ru.practicum.ewmServer.requests.model.ParticipationRequestModel;
import ru.practicum.ewmServer.requests.storage.RequestsRepository;
import ru.practicum.ewmServer.users.model.UserModel;
import ru.practicum.ewmServer.users.service.UsersService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
public class RequestsServiceImpl implements RequestsService {
    private final RequestsRepository requestsRepository;
    private final UsersService usersService;
    private final EventsRepository eventsRepository;
    private final ModelMapper modelMapper;

    @Override
    public ParticipationRequestDto createRequestFromUser(Long userId, Long eventId) throws NotFoundException, ConflictException {
        log.info("Run createRequestFromUser");
        UserModel user = usersService.getUser(userId);
        EventModel event = eventsRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("No event id = " + eventId)
        );

        log.info("Run checks");
        throwExceptionIfIllegalRequest(event, userId);

        ParticipationRequestModel request = ParticipationRequestModel.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();
        log.info("Update:");
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.info("Set state: {} -> {} and {}", RequestStatus.CONFIRMED, event.getRequestModeration(), event.getParticipantLimit());
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequestsCount(event.getConfirmedRequestsCount() + 1);
            eventsRepository.save(event);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        log.info("Save");
        ParticipationRequestModel saved = requestsRepository.save(request);
        log.info("Return");
        return modelMapper.map(saved, ParticipationRequestDto.class);
    }

    @Override
    public List<ParticipationRequestDto> getAllByUserIdRequestFromUser(Long userId) throws NotFoundException {
        UserModel user = usersService.getUser(userId);
        return requestsRepository.findAllByRequesterOrderByIdAsc(user).stream()
                .map(p -> modelMapper.map(p, ParticipationRequestDto.class))
                .collect(Collectors.toList());
    }

    /**
     * <B>Отмена собственного запроса</B>
     *
     * @param userId
     * @param requestId
     * @return
     * @throws NotFoundException
     * @throws ConflictException
     */
    @Override
    public ParticipationRequestDto setCancelRequestFromUser(
            Long userId, Long requestId
    ) throws NotFoundException, ConflictException {
        UserModel user = usersService.getUser(userId);
        ParticipationRequestModel request = getUserRequest(requestId, userId);
        EventModel event = request.getEvent();
        if (request.getStatus() == RequestStatus.CONFIRMED) {
            throw new ConflictException("Request id = " + requestId + " is already confirmed.");
        }
        log.info("Updating request and request count");
        request.setStatus(RequestStatus.CANCELED);
        event.setConfirmedRequestsCount(event.getConfirmedRequestsCount() - 1);
        eventsRepository.save(event);
        request = requestsRepository.save(request);
        return modelMapper.map(request, ParticipationRequestDto.class);
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestAsInitiator(Long userId, Long eventId) throws NotFoundException {
        UserModel user = usersService.getUser(userId);
        List<ParticipationRequestModel> list = requestsRepository.getAllByInitiatorId(user.getId());
        return list.stream()
                .map(p -> modelMapper.map(p, ParticipationRequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RequestStatusUpdateResponseDto updateStatusRequestFromInitiator(
            Long userId, Long eventId, RequestStatusUpdateRequestDto toUpdate
    ) throws NotFoundException, ConflictException {
        EventModel event = eventsRepository.getReferenceById(eventId);
        RequestStatus setSate = toUpdate.getStatus();
        Set<Long> idToUpdate = toUpdate.getRequestIds();

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException(
                    String.format("Event id = %s can't been updated by userId = %s", eventId, userId));
        }

        log.info("Update response: Confirmed {} limit {}}", event.getConfirmedRequestsCount(), event.getParticipantLimit());
        if (event.getConfirmedRequestsCount() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("No one free slots on event id = %s", eventId));
        }

        List<Long> okIdList = new ArrayList<>();
        List<Long> baIddList = new ArrayList<>();

        List<ParticipationRequestModel> allPendingRequests = requestsRepository.getAllByStatusAndEventId(RequestStatus.PENDING, eventId);
        List<Long> allPendingRequestsIds = allPendingRequests.stream()
                .map(ParticipationRequestModel::getId)
                .collect(Collectors.toList());

        // no one has wrong state
        log.info("To update ids: {} all: {}", idToUpdate, allPendingRequestsIds);
        for (Long x : idToUpdate) {
            if (!allPendingRequestsIds.contains(x)) {
                throw new ConflictException(String.format("Can't update, request id = %s has an wrong state", eventId));
            }
        }
        // Have Slots
        if ((event.getConfirmedRequestsCount() + idToUpdate.size() > event.getParticipantLimit() && event.getParticipantLimit() != 0)
                || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))

        ) {
            throw new ConflictException(
                    String.format("Event %s is closed or not ready for registration %s participants", eventId, idToUpdate.size())
            );
        }
        // need to reject others
        if (idToUpdate.size() <= allPendingRequestsIds.size() && setSate.equals(RequestStatus.CONFIRMED)) {
            okIdList = new ArrayList<>(idToUpdate);
        } else {
            if (setSate.equals(RequestStatus.CONFIRMED)) {
                for (Long x : allPendingRequestsIds) {
                    if (!idToUpdate.contains(x)) {
                        baIddList.add(x);
                    }
                }
            } else {
                baIddList = new ArrayList<>(idToUpdate);
            }
        }

        requestsRepository.updateRequestsStatus(okIdList, baIddList, RequestStatus.CONFIRMED.toString(), RequestStatus.REJECTED.toString());

        List<ParticipationRequestDto> okList = new ArrayList<>();
        List<ParticipationRequestDto> badList = new ArrayList<>();

        if (!okIdList.isEmpty()) {
            okList = requestsRepository.findAllByIdIn(okIdList).stream()
                    .map(e -> modelMapper.map(e, ParticipationRequestDto.class))
                    .collect(Collectors.toList());
        }
        if (!baIddList.isEmpty()) {
            badList = requestsRepository.findAllByIdIn(baIddList).stream()
                    .map(e -> modelMapper.map(e, ParticipationRequestDto.class))
                    .collect(Collectors.toList());
        }

        event.setConfirmedRequestsCount(event.getConfirmedRequestsCount() + idToUpdate.size());
        eventsRepository.save(event);
        return RequestStatusUpdateResponseDto.builder()
                .confirmedRequests(okList)
                .rejectedRequests(badList)
                .build();
    }

    @Override
    public ParticipationRequestModel getUserRequest(Long id, Long userId) throws NotFoundException {
        Optional<ParticipationRequestModel> user = requestsRepository.findByIdAndRequester_id(id, userId);
        if (user.isPresent()) {
            return user.get();
        }
        String msg = String.format("Request 'id' %s.  of userId %s is not exists", id, userId);
        log.info(msg);
        throw new NotFoundException(msg);
    }

    private void throwExceptionIfIllegalRequest(EventModel event, Long userId) throws ConflictException {
        Long eventId = event.getId();
        log.info("Check initiator");
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            String msg = String.format("Cant add request to Event eventId = %s from initiator %s", eventId, userId);
            log.info(msg);
            throw new ConflictException(msg);
        }
        log.info("Check allowed");
        if (!event.getState().equals(EventStatus.PUBLISHED)
                || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            String msg = "Event is closed or not ready for registration.";
            log.info(msg);
            throw new ConflictException(msg);
        }
        log.info("Check free slots");
        if (event.getConfirmedRequestsCount() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            String msg = String.format("Event eventId = %s has no free slots", eventId);
            log.info(msg);
            throw new ConflictException(msg);
        }
        log.info("Check doubles");
        Optional<ParticipationRequestModel> firstRequest = requestsRepository.findByRequesterIdAndEventId(userId, eventId);
        if (firstRequest.isPresent()) {
            if (firstRequest.get().getStatus() != RequestStatus.REJECTED) {
                String msg = "Cant add same double request";
                log.info(msg);
                throw new ConflictException(msg);
            }
        }
    }

    private ParticipationRequestModel getRequest(Long id) throws NotFoundException {
        Optional<ParticipationRequestModel> user = requestsRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        String msg = String.format("Request 'id' %s. is not exists", id);
        log.info(msg);
        throw new NotFoundException(msg);
    }

}
