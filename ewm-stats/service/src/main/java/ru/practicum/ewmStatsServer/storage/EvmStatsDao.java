package ru.practicum.ewmStatsServer.storage;

import ru.practicum.ewmStatsDto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EvmStatsDao {
    List<StatDto> getStats(LocalDateTime from, LocalDateTime to, List<String> uriList, Boolean emptyList, Boolean uniqueIp);
}
