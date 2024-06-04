package ru.practicum.ewmServer.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmServer.categories.service.CategoriesService;
import ru.practicum.ewmServer.error.exceptions.BadRequestException;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.dto.*;
import ru.practicum.ewmServer.events.dto.sesrch.EventAdminSearchRequest;
import ru.practicum.ewmServer.events.dto.sesrch.EventSearchRequest;
import ru.practicum.ewmServer.events.enums.EventStatus;
import ru.practicum.ewmServer.events.model.EventModel;
import ru.practicum.ewmServer.events.storage.EventsRepository;
import ru.practicum.ewmServer.requests.emums.RequestStatus;
import ru.practicum.ewmServer.requests.model.EventRequestsCountModel;
import ru.practicum.ewmServer.requests.storage.RequestsRepository;
import ru.practicum.ewmServer.stats.service.StatisticService;
import ru.practicum.ewmServer.users.service.UsersService;
import ru.practicum.ewmStatsDto.HitInDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewmServer.events.enums.EventStatus.*;
//import static ru.practicum.ewmServer.emums.RequestStatus.CONFIRMED;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
public class EventsServiceImpl implements EventsService {
    private static final String SERVICE_NAME = "ewm-service";

    private final EventsRepository eventsRepository;
    private final UsersService usersService;
    private final CategoriesService categoriesService;
    private final RequestsRepository requestsRepository;
    private final StatisticService statisticService;
    private final ModelMapper modelMapper;

    @Override
    public EventModel getEvent(Long id) throws NotFoundException {
        Optional<EventModel> event = eventsRepository.findById(id);
        if (event.isPresent()) {
            return event.get();
        }
        String msg = String.format("Event 'id' %s. is not exists", id);
        log.info(msg);
        throw new NotFoundException(msg);
    }

    /**
     * <B>ADMIN</B>
     * Эндпоинт возвращает полную информацию обо всех событиях подходящих под переданные условия
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список
     *
     * @param filter
     * @return
     */
    @Override
    public List<EventFullDto> adminGetAll(EventAdminSearchRequest filter) throws BadRequestException {
        if (Objects.nonNull(filter.getRangeEnd()) && Objects.nonNull(filter.getRangeStart())) {
            if (filter.getRangeEnd().isBefore(filter.getRangeStart())) {
                throw new BadRequestException("Range date is incorrect");
            }
        }
        if (Objects.nonNull(filter.getCategories())) {
            for (long v : filter.getCategories()) {
                if (v < 1) {
                    throw new BadRequestException("Categories starts from id 1");
                }
            }
        }

        Integer stateId = Objects.nonNull(filter.getStates()) ? filter.getStates().ordinal() : null;
        List<Long> categories = Objects.nonNull(filter.getCategories()) ? filter.getCategories() : List.of(0L);
        List<Long> users = Objects.nonNull(filter.getUsers()) ? filter.getUsers() : List.of(0L);
        List<EventModel> eventsList = eventsRepository.getAllBySearchRequest(
                null,
                //categories.size(),
                categories,
                null,
                filter.getRangeStart(),
                filter.getRangeEnd(),
                null,
                null,
                filter.getFrom(),
                filter.getSize(),
                stateId,
                //users.size(),
                users
        );

        return eventsList.stream()
                .map(e -> modelMapper.map(e, EventFullDto.class))
                .collect(Collectors.toList());
    }

    /**
     * <B>ADMIN</B>
     * Редактирование данных любого события администратором. Валидация данных не требуется. Обратите внимание:
     * <p>
     * дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     *
     * @param eventId
     * @param newEventDto
     * @return
     * @throws NotFoundException
     * @throws ConflictException
     */
    @Override
    public EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequestDto newEventDto)
            throws NotFoundException, ConflictException {
        EventModel oldEvent = getEvent(eventId);
        EventModel eventUpdates = modelMapper.map(newEventDto, EventModel.class);
        if (LocalDateTime.now().plusHours(1).isAfter(oldEvent.getEventDate())) {
            throw new ConflictException("Cant update state event: 1 h to start");
        }

        ModelMapper modelMapperNoNulls = new ModelMapper();
        modelMapperNoNulls.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapperNoNulls.map(eventUpdates, oldEvent);

        if (Objects.nonNull(newEventDto.getStateAction())) {
            switch (newEventDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (!oldEvent.getState().equals(PENDING)) {
                        throw new ConflictException("Cant update state event is not in PENDING state");
                    }
                    oldEvent.setState(PUBLISHED);
                    break;
                case REJECT_EVENT:
                    if (oldEvent.getState().equals(PUBLISHED)) {
                        throw new ConflictException("Cant update state event is in PUBLISHED state");
                    }
                    oldEvent.setState(CANCELED);
                    break;
                default:
                    throw new ConflictException("Unknown admin action state type" + newEventDto.getStateAction().toString());
            }
        }
        log.info(String.format("\nUpdating pdate : \n %s \n TO \n %s \n f", oldEvent, oldEvent));
        EventModel saved = eventsRepository.save(oldEvent);
        log.info(String.format("Saved event is %s \n", saved));
        return modelMapper.map(saved, EventFullDto.class);
    }


    @Override
    @Transactional
    public EventFullDto userCreateEvent(Long userId, NewEventDto newEventDto) throws NotFoundException, ConflictException {
        EventModel newEvent = modelMapper.map(newEventDto, EventModel.class);
        newEvent.setInitiator(usersService.getUser(userId));
        newEvent.setCategory(categoriesService.getCategory(newEventDto.getCategory()));
        newEvent.setState(PENDING);
        EventModel saved = eventsRepository.save(newEvent);
        log.info(String.format("saved %s. is", saved));
        return modelMapper.map(saved, EventFullDto.class);
    }

    /**
     * <B>Изменение пользователем</B>
     * изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
     * дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
     *
     * @param userId
     * @param eventId
     * @param newEventDto
     * @return
     * @throws ConflictException
     * @throws NotFoundException
     */
    @Override
    public EventFullDto userUpdate(Long userId, Long eventId, UpdateEventUserRequestDto newEventDto)
            throws ConflictException, NotFoundException {
        EventModel oldEvent = getEvent(eventId);
        EventModel eventUpdates = modelMapper.map(newEventDto, EventModel.class);

        if (oldEvent.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new ConflictException("Can't update, event starts soon");
        }
        if (oldEvent.getState() == CANCELED || oldEvent.getState() == PENDING) {
            ModelMapper modelMapperNoNulls = new ModelMapper();
            modelMapperNoNulls.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
            modelMapperNoNulls.map(eventUpdates, oldEvent);

            if (Objects.nonNull(newEventDto.getStateAction())) {
                switch (newEventDto.getStateAction()) {
                    case SEND_TO_REVIEW:
                        oldEvent.setState(PENDING);
                        break;
                    case CANCEL_REVIEW:
                        oldEvent.setState(CANCELED);
                        break;
                    default:
                        throw new ConflictException("Unknown user action state type" + newEventDto.getStateAction().toString());
                }
            }
            log.info(String.format("\nUPDATE: \n %s \n TO \n %s \n f", eventUpdates, oldEvent));
            EventModel saved = eventsRepository.save(oldEvent);
            log.info(String.format("saved event is %s \n", saved));
            return modelMapper.map(saved, EventFullDto.class);
        } else {
            throw new ConflictException("Can't update, event is in un editable state");
        }
    }

    /**
     * <B>PUBLIC: Получение событий с возможностью фильтрации </B>
     *
     * <P>это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
     * текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
     * если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
     * информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
     * информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если по заданным фильтрам не найдено ни одного события, возвращает пустой список</p>
     *
     * @param request
     * @return
     */
    @Override
    public List<EventShortDto> publicGetFilteredEvents(
            EventSearchRequest filter,
            HttpServletRequest request
    ) throws BadRequestException {
        if (Objects.nonNull(filter.getRangeEnd()) && Objects.nonNull(filter.getRangeStart())) {
            if (filter.getRangeEnd().isBefore(filter.getRangeStart())) {
                throw new BadRequestException("Range date is incorrect");
            }
        }
        if (Objects.nonNull(filter.getCategories())) {
            for (long v : filter.getCategories()) {
                if (v < 1) {
                    throw new BadRequestException("Categories starts from id 1");
                }
            }
        }

        List<Long> categories = Objects.nonNull(filter.getCategories()) ? filter.getCategories() : List.of(0L);
        List<Long> users = List.of(0L);

        List<EventModel> eventsList = eventsRepository.getAllBySearchRequest(
                filter.getText(),
                categories,
                filter.getPaid(),
                filter.getRangeStart(),
                filter.getRangeEnd(),
                filter.getOnlyAvailable(),
                filter.getSort(),
                filter.getFrom(),
                filter.getSize(),
                EventStatus.valueOf("PUBLISHED").ordinal(),
                users
        );

        HitInDto hitInDto = HitInDto.builder()
                .uri(request.getRequestURI())
                .app(SERVICE_NAME)
                .ip(request.getRemoteAddr())
                .build();
        statisticService.save(hitInDto);

        return eventsList.stream()
                .map(e -> modelMapper.map(e, EventShortDto.class))
                .collect(Collectors.toList());
    }

    /**
     * <B>PUBLIC: Получение подробной информации об опубликованном событии по его идентификатору</B>
     *
     * <p>событие должно быть опубликовано
     * информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
     * информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     * В случае, если события с заданным id не найдено, возвращает статус код 404</p>
     *
     * @param eventId
     * @param request
     * @return
     */
    @Override
    public EventFullDto publicGetById(Long eventId, HttpServletRequest request)
            throws NotFoundException, JsonProcessingException {

        EventModel event = getEvent(eventId, true);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Event is not in PUBLISHED state");
        }
        EventFullDto retEvent = modelMapper.map(event, EventFullDto.class);

        Map<Long, Long> statistics = statisticService.getStats(
                event.getCreatedDate(), LocalDateTime.now(),
                Optional.of(List.of(request.getRequestURI())),
                true);

        if (!statistics.isEmpty()) {
            retEvent.setViews(statistics.get(event.getId()));
        } else {
            retEvent.setViews(0L);
        }

        List<EventRequestsCountModel> rqst = requestsRepository.getEventRequestCount(List.of(eventId), RequestStatus.CONFIRMED);
        if (!rqst.isEmpty()) {
            Long confirmedRequests = requestsRepository.getEventRequestCount(List.of(eventId), RequestStatus.CONFIRMED).get(0).getCount();
            retEvent.setConfirmedRequests(confirmedRequests);
        }

        HitInDto hitInDto = HitInDto.builder()
                .uri(request.getRequestURI())
                .app(SERVICE_NAME)
                .ip(request.getRemoteAddr())
                .build();
        log.info(String.format("Save stat: %s \n", hitInDto));
        statisticService.save(hitInDto);
        event.setViews(retEvent.getViews());
        return retEvent;
    }

    @Override
    public List<EventShortDto> getAllEventsByUserIdPage(Long userId, Integer from, Integer size) throws NotFoundException {
        usersService.getUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id")); //.ascending()

        return eventsRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(e -> modelMapper.map(e, EventShortDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto userGetById(Long userId, Long eventId) throws NotFoundException {
        Optional<EventModel> optEvent = eventsRepository.findByIdAndInitiatorId(eventId, userId);
        if (optEvent.isEmpty()) {
            throw new NotFoundException(String.format("Cant find eventId = %s of userId = %s", eventId, userId));
        }
        EventModel event = optEvent.get();
        return modelMapper.map(event, EventFullDto.class);
    }

    private EventModel getEvent(Long eventId, Boolean isPublishedOnly) throws NotFoundException {
        if (isPublishedOnly) {
            return eventsRepository.findByIdAndState(
                    eventId, PUBLISHED
            ).orElseThrow(() -> new NotFoundException(String.format("Seams that event eventId = %s is not published", eventId)));
        }
        return eventsRepository.findById(
                eventId
        ).orElseThrow(() -> new NotFoundException(String.format("Event eventId = %s is not exists", eventId)));
    }

}
