package ru.practicum.ewmServer.likes.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmServer.likes.dto.EventTopChartDto;
import ru.practicum.ewmServer.likes.model.LikesModel;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface EventLikesRepository extends JpaRepository<LikesModel, Long> {

    Optional<LikesModel> findByUserIdAndEventId(Long eventId, Long userId);

    @Query(value = " WITH s AS (\n" +
            "                 SELECT row_number() OVER (\n" +
            "                              ORDER BY grouping (l.event_id) + grouping (e.initiator_id) + grouping (c.id) desc\n" +
            "                                     , sum(rating) desc, c.name, u.name, e.title\n" +
            "                          ) as npp \n" +
            "                        , l.event_id as eventId\n" +
            "                        , case \n" +
            "                            when grouping (l.event_id) = 1 and grouping (e.initiator_id) = 1 and grouping (c.id) = 1 then 'Total:'\n" +
            "                            when grouping (l.event_id) = 1 and grouping (e.initiator_id) = 0 and grouping (c.id) = 0 then 'Total by category:'\n" +
            "                            when grouping (l.event_id) = 1 and grouping (e.initiator_id) = 0 and grouping (c.id) = 1 then 'Total by user:'\n" +
            "                            else e.title \n" +
            "                          end as title \n" +
            "                        , e.description \n" +
            "                        , c.name as categoryName \n" +
            "                        , c.id as categoryId \n" +
            "                        , e.initiator_id as userId \n" +
            "                        ,  u.name as userName \n" +
            "                        , sum(l.rating) as rating \n" +
            "                        , sum(l.likes)  as countLikes \n" +
            "                        , sum(l.dislikes) as countDislike \n" +
            "                        , count(*) as countEvents \n" +
            "                 FROM v_event_rating l \n" +
            "                 JOIN events e ON l.event_id = e.id \n" +
            "                 JOIN categories c ON c.id  = e.category_id \n" +
            "                 JOIN users u on e.initiator_id = u.id \n" +
            "                WHERE (e.id in (:eventIds) or 0 in (:eventIds)) \n" +
            "                  AND (e.initiator_id in (:userIds) or 0 in (:userIds)) \n" +
            "                  AND (:onlyFuture is false OR (:onlyFuture is true AND e.event_date >= NOW())) \n" +
            "                  AND (:onlyAvailable is false OR " +
            "                           (:onlyAvailable is true AND " +
            "                               (participant_limit > confirmed_Requests OR participant_limit = 0)" +
            "                         )) \n" +
            "                GROUP BY GROUPING SETS (\n" +
            "                    (c.name , c.id, e.initiator_id, u.name, l.event_id, e.title, e.description)\n" +
            "                    , (c.name , c.id, e.initiator_id, u.name)\n" +
            "                    , (e.initiator_id, u.name)\n" +
            "                    ,() \n" +
            "                      ) \n" +
            ") SELECT  " +
            "          npp, eventId \n" +
            "         ,title, description \n" +
            "         ,categoryName, categoryId \n" +
            "         ,userId, userName, rating \n" +
            "         ,countLikes, countDislike, countEvents \n" +
            "  FROM s " +
            " LIMIT :size OFFSET :from \n",
            nativeQuery = true)
    List<EventTopChartDto> getEventsTopChart(
            @Param("eventIds") List<Long> eventIds,
            @Param("userIds") List<Long> userIds,
            @Param("onlyFuture") boolean onlyFuture,
            @Param("onlyAvailable") boolean onlyAvailable,
            @Param("from") Integer from,
            @Param("size") Integer size
    );

}
