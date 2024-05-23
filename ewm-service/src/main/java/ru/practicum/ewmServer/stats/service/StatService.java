package ru.practicum.ewmServer.stats.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.ewmStatsDto.HitInDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatService {
    void save(HitInDto hitInDto);

    ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris, Boolean unique);
}
