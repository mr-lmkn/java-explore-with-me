package ru.practicum.ewmStatsServer.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmStatsDto.HitInDto;
import ru.practicum.ewmStatsDto.HitOutDto;
import ru.practicum.ewmStatsDto.StatDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EwmStatsService {
    @Transactional
    HitOutDto saveHit(HitInDto hitInDto);

    List<StatDto> getStats(LocalDateTime start, LocalDateTime end, Optional<List<String>> urisList, boolean uniqueIp);
}
