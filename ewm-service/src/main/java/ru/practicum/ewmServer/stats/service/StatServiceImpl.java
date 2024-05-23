package ru.practicum.ewmServer.stats.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewmStatsDto.HitInDto;
import ru.practicum.statsClient.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StatServiceImpl implements StatService{
    private String StatsServiceUrl;
    private static final String SERVICE_NAME = "ewm-service";
    private RestTemplateBuilder restTemplateBuilder;
    private StatsClient statsClient;

    public StatServiceImpl(@Value("${stats-service.STATS_CLIENT_URL}") String serverUrl, RestTemplateBuilder builder) {
        StatsServiceUrl = serverUrl;
        statsClient = new StatsClient(serverUrl, builder);
    }

    public void save(HitInDto hitInDto) {
        log.info("Send to: {} -> {}", StatsServiceUrl, hitInDto);
        statsClient.save(hitInDto.getApp(), hitInDto.getUri(), hitInDto.getIp());
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris, Boolean unique) {
        log.info("Get from: {}", StatsServiceUrl);
        ResponseEntity<Object> ret = statsClient.getStats(start, end, uris, unique);
        log.info(ret.toString());
        return statsClient.getStats(start, end, uris, unique);
    }

}
