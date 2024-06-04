package ru.practicum.ewmServer.requests.dto.statusUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmServer.requests.emums.RequestStatus;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusUpdateRequestDto {
    private Set<Long> requestIds;
    private RequestStatus status;
}
