package ru.practicum.ewmServer.requests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewmServer.events.model.EventModel;

@Data
@Builder
@AllArgsConstructor
public class EventRequestsCountModel {
    private EventModel event;
    private Long count;
}
