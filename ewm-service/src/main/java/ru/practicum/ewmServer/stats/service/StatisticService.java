package ru.practicum.ewmServer.stats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import ru.practicum.ewmStatsDto.HitInDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StatisticService {
    void save(HitInDto hitInDto);

    ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris, Boolean unique);

    Map<Long, Long> getStats(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris, Boolean unique)
            throws JsonProcessingException;
}
