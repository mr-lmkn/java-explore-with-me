package ru.practicum.ewmServer.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import ru.practicum.ewmServer.dtoValidateGroups.GroupCreate;
import ru.practicum.ewmServer.dtoValidateGroups.GroupUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    @NotBlank(groups = {GroupCreate.class})
    @Size(groups = {GroupCreate.class, GroupUpdate.class}, min = 1, max = 50)
    private String title;
    @Nullable
    private Boolean pinned = false;
    @Nullable
    private Set<Long> events;
}
