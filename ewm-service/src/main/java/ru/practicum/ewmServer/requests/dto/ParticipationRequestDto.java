package ru.practicum.ewmServer.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmServer.config.constants.Constants;
import ru.practicum.ewmServer.requests.emums.RequestStatus;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private LocalDateTime created;
    @Positive
    private Long event;
    @Positive
    private Long id;
    @Positive
    private Long requester;
    private RequestStatus status;
}
