package ru.practicum.ewmServer.events.storage;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmServer.events.enums.EventStatus;
import ru.practicum.ewmServer.events.enums.EventsSort;
import ru.practicum.ewmServer.events.model.EventModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DynamicUpdate
@EnableJpaRepositories
public interface EventsRepository extends JpaRepository<EventModel, Long> {

    List<EventModel> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<EventModel> findByCategoryId(Long id);

    Optional<EventModel> findByIdAndState(Long eventId, EventStatus state);

    Optional<EventModel> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query(value = "\n SELECT e.*, rt.rating \n"
            + "       FROM events e\n"
            + "  LEFT JOIN v_event_rating rt ON e.id = rt.event_id \n"
            + "      WHERE (:status is null OR e.status_id = :status) \n"
            + "        AND (coalesce(:text, null) is null \n"
            + "               OR upper(title) LIKE '%'||upper(:text)||'%' "
            + "               OR upper(e.annotation) LIKE ('%'||upper(:text)||'%') \n"
            + "               OR upper(e.description) LIKE ('%'||upper(:text)||'%') ) \n"
            + "        AND ( 0 in (:categories) OR e.category_id IN (:categories)) \n"
            + "        AND (:paid is null      OR e.paid = :paid)\n"
            + "        AND ( (cast(:rangeStart as TIMESTAMP) is null AND e.event_date >= NOW()) \n"
            + "               OR e.event_date >= cast(:rangeStart as TIMESTAMP) ) \n"
            + "        AND (cast(:rangeEnd as TIMESTAMP) is null   OR e.event_date <= cast(:rangeEnd as TIMESTAMP)) \n"
            + "        AND (coalesce(:onlyAvailable, false) is false OR participant_limit > confirmed_Requests) \n"
            + "        AND ( 0 in (:usersIds) OR initiator_id IN (:usersIds)) \n"
            + "   ORDER BY \n"
            + "         CASE WHEN :sort = 'EVENT_DATE' THEN EXTRACT(EPOCH FROM e.EVENT_DATE) * 1000 \n"
            + "              WHEN :sort = 'VIEWS' THEN e.VIEWS  \n"
            + "              WHEN :sort = 'RATING' THEN CAST(rt.rating  AS BIGINT) END desc nulls last\n"
            + "       , CASE WHEN :sort is null THEN e.id ELSE NULL END \n"
            + "      LIMIT :size OFFSET :from \n",
            nativeQuery = true)
    ArrayList<EventModel> getAllBySearchRequest(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            @Param("sort") EventsSort sort,
            @Param("from") Integer from,
            @Param("size") Integer size,
            @Param("status") Integer status,
            @Param("usersIds") List<Long> usersIds
    );

}
