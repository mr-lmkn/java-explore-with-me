package ru.practicum.ewmServer.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @NotBlank(message = "Поле 'E-mail' не заполнено")
    @Email(regexp = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+"
            + "(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@"
            + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "Поле e-mail должно содержать валидный адрес")
    private String email;
}
