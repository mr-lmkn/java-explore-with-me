package ru.practicum.ewmServer.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutCategoryDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
