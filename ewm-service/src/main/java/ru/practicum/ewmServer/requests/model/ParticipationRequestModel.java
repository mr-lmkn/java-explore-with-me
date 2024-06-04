package ru.practicum.ewmServer.requests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmServer.events.model.EventModel;
import ru.practicum.ewmServer.requests.emums.RequestStatus;
import ru.practicum.ewmServer.users.model.UserModel;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity(name = "requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventModel event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private UserModel requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

}
