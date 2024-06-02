package ru.practicum.ewmServer.events.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.ewmServer.events.enums.EventAdminStateAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateEventAdminRequestDto extends NewEventDto {
    private EventAdminStateAction stateAction;
}
