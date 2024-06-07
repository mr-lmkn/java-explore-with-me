package ru.practicum.ewmServer.likes.service;

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
import ru.practicum.ewmServer.likes.dto.EventTopChartDto;
import ru.practicum.ewmServer.likes.dto.RateDto;
import ru.practicum.ewmServer.likes.model.LikesModel;
import ru.practicum.ewmServer.likes.storage.EventLikesRepository;
import ru.practicum.ewmServer.users.model.UserModel;
import ru.practicum.ewmServer.users.storage.UsersRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
public class EventRatingsServiceImpl implements EventRatingsService {
    private final EventLikesRepository eventLikesRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;
    private final ModelMapper modelMapper;

    /**
     * <P>Сохраняет оценку с перезаписью оценку</P>
     *
     * @param eventId
     * @param userId
     * @param isLike
     * @return
     * @throws NotFoundException
     */

    @Override
    public RateDto rateEvent(Long eventId, Long userId, Boolean isLike) throws NotFoundException, ConflictException {
        UserModel user = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Unknown user id %s", userId)));
        EventModel event = eventsRepository.findByIdAndState(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event is not PUBLISHED id %s", eventId)));
        Optional<LikesModel> existedRate = eventLikesRepository.findByUserIdAndEventId(userId, eventId);
        LikesModel saved;

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ConflictException("Сan’t set the reaction for your own events");
        }

        if (existedRate.isPresent()) {
            if (Objects.equals(existedRate.get().getIsLike(), isLike)) {
                throw new ConflictException("Can't set the same reaction twice");
            }
            existedRate.get().setIsLike(isLike);
            saved = eventLikesRepository.save(existedRate.get());
        } else {
            LikesModel likesModel = LikesModel.builder()
                    .user(user)
                    .event(event)
                    .isLike(isLike)
                    .build();
            saved = eventLikesRepository.save(likesModel);
        }
        return modelMapper.map(saved, RateDto.class);
    }

    @Override
    public void deleteRateEvent(Long eventId, Long userId) throws NotFoundException {
        usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Unknown user id %s", userId)));
        eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Unknown event id %s", eventId)));
        LikesModel toDelete = eventLikesRepository.findByUserIdAndEventId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Rate is not exists (userId = %s, eventId = %s)", userId, eventId)));
        eventLikesRepository.delete(toDelete);
    }

    @Override
    public List<EventTopChartDto> getEventsTopChart(
            List<Long> eventIds, List<Long> userIds,
            Boolean onlyAvailable, Boolean onlyFuture,
            Integer from, Integer size
    ) {

        List<EventTopChartDto> ret = eventLikesRepository.getEventsTopChart(eventIds, userIds, onlyFuture, onlyAvailable, from, size);

        return ret;
/*
        return ret.stream()
                .map(p -> modelMapper.map(p, EventRatingTopChartDto.class))
                .collect(Collectors.toList()); */
    }

}
