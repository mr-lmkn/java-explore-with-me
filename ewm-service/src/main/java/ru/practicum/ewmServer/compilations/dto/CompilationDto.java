package ru.practicum.ewmServer.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import ru.practicum.ewmServer.events.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    @NotBlank
    private String title;
    @Nullable
    private List<EventShortDto> events;
    @Nullable
    private Boolean pinned;
}
