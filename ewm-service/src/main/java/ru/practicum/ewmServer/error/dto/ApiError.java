package ru.practicum.ewmServer.error.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;
import ru.practicum.ewmServer.config.constants.Constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class ApiError {

    private HttpStatus status;
    private String message;
    private List<String> errors;
    private String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT)
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ApiError(HttpStatus status, String message, String reason, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.reason = reason;
    }

    public ApiError(HttpStatus status, String message, String reason, String error) {
        super();
        this.status = status;
        this.message = message;
        this.reason = reason;
        errors = Arrays.asList(error);
    }
}
