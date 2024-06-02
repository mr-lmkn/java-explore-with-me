package ru.practicum.ewmServer.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmServer.dtoValidateGroups.GroupCreate;
import ru.practicum.ewmServer.dtoValidateGroups.GroupUpdate;
import ru.practicum.ewmServer.events.dto.location.LocationDto;
import ru.practicum.ewmServer.events.validations.NotSoLate2hDate;
import ru.practicum.ewmStatsDto.Constants;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank(groups = {GroupCreate.class})
    @Size(groups = {GroupCreate.class, GroupUpdate.class}, min = 20, max = 2000)
    private String annotation;
    @NotNull(groups = {GroupCreate.class})
    @Positive(groups = {GroupCreate.class, GroupUpdate.class})
    private Long category;
    @NotSoLate2hDate(groups = {GroupCreate.class, GroupUpdate.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private LocalDateTime eventDate;
    @NotBlank(groups = {GroupCreate.class})
    @Size(groups = {GroupCreate.class, GroupUpdate.class}, min = 20, max = 7000)
    private String description;
    @NotNull(groups = {GroupCreate.class})
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero(groups = {GroupCreate.class, GroupUpdate.class})
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank(groups = {GroupCreate.class})
    @Size(groups = {GroupCreate.class, GroupUpdate.class}, min = 3, max = 120)
    private String title;
}
