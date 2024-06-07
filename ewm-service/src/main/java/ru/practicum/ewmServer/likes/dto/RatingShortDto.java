package ru.practicum.ewmServer.likes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingShortDto {
    private Long eventId;
    private Integer rating;
}
