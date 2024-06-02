package ru.practicum.ewmServer.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmServer.categories.dto.CategoryDto;
import ru.practicum.ewmServer.config.constants.Constants;
import ru.practicum.ewmServer.events.dto.location.LocationDto;
import ru.practicum.ewmServer.users.dto.UserShortDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    @NotBlank
    private String annotation;
    @NotBlank
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private LocalDateTime createdOn;
    @Size(max = 7000, min = 20)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    @FutureOrPresent(message = "Значение поля 'eventDate' не может быть в прошлом")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    @NotNull
    private LocationDto location;
    @NotBlank
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long views;
}
