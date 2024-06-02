package ru.practicum.ewmServer.requests.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmServer.requests.emums.RequestStatus;
import ru.practicum.ewmServer.requests.model.EventRequestsCountModel;
import ru.practicum.ewmServer.requests.model.ParticipationRequestModel;
import ru.practicum.ewmServer.users.model.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface RequestsRepository extends JpaRepository<ParticipationRequestModel, Long> {
    ArrayList<ParticipationRequestModel> findAllByRequesterOrderByIdAsc(UserModel owner);

    @Query("SELECT new ru.practicum.ewmServer.requests.model.EventRequestsCountModel(pr.event, COUNT(DISTINCT pr.id)) \n"
            + "  FROM requests AS pr \n"
            + " WHERE pr.event.id IN (:idList) \n"
            + "   AND pr.status = :status \n"
            + " GROUP BY (pr.event)\n")
    List<EventRequestsCountModel> getEventRequestCount(
            @Param("idList") List<Long> idList,
            @Param("status") RequestStatus status);

    Optional<ParticipationRequestModel> findByIdAndRequester_id(Long id, Long userId);

    ArrayList<ParticipationRequestModel> findAllByRequester(UserModel user);

    Optional<ParticipationRequestModel> findByRequesterIdAndEventId(Long userId, Long eventId);

    @Query(value = "SELECT r.* \n"
            + "  FROM requests r JOIN events e ON e.id = r.event_id \n"
            + " WHERE e.initiator_id = :user  \n",
            nativeQuery = true
    )
    List<ParticipationRequestModel> getAllByInitiatorId(
            @Param("user") Long userId
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE requests SET \n "
            + "     status = CASE WHEN id in :okIdList THEN :ok ELSE :notOk END \n"
            + " WHERE id in :okIdList OR id in :baIdList \n",
            nativeQuery = true)
    void updateRequestsStatus(
            @Param("okIdList") List<Long> okIdList,
            @Param("baIdList") List<Long> baIdList,
            @Param("ok") String ok,
            @Param("notOk") String notOk);

    ArrayList<ParticipationRequestModel> findAllByIdIn(List<Long> okIdList);

    List<ParticipationRequestModel> getAllByStatusAndEventId(RequestStatus requestStatus, Long eventId);

}
