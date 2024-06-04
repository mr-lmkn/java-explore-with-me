package ru.practicum.ewmServer.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.ewmServer.events.enums.EventUserStateAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateEventUserRequestDto extends NewEventDto {
    private EventUserStateAction stateAction;
}
