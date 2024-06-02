package ru.practicum.ewmServer.stats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewmStatsDto.HitInDto;
import ru.practicum.ewmStatsDto.StatDto;
import ru.practicum.statsClient.StatsClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticServiceImpl implements StatisticService {
    private static final String SERVICE_NAME = "ewm-service";
    private final String statsServiceUrl;
    private final StatsClient statsClient;
    private RestTemplateBuilder restTemplateBuilder;


    public StatisticServiceImpl(@Value("${stats-service.STATS_CLIENT_URL}") String serverUrl,
                                RestTemplateBuilder builder, RestTemplateBuilder restTemplateBuilder) {
        this.statsServiceUrl = serverUrl;
        this.restTemplateBuilder = restTemplateBuilder;
        statsClient = new StatsClient(serverUrl, builder);
    }

    public void save(HitInDto hitInDto) {
        log.info("Send to: {} -> {}", statsServiceUrl, hitInDto);
        statsClient.save(hitInDto.getApp(), hitInDto.getUri(), hitInDto.getIp());
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris, Boolean unique) {
        log.info("Get from: {}", statsServiceUrl);
        ResponseEntity<Object> ret = statsClient.getStats(start, end, uris, unique);
        log.info(ret.toString());
        return statsClient.getStats(start, end, uris, unique);
    }

    public Map<Long, Long> getStats(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris, Boolean unique)
            throws JsonProcessingException {
        log.info("Get stats from: {}", statsServiceUrl);
        ResponseEntity<Object> response = get(start, end, uris, unique);
        log.info(String.format("Response : %s \n", response.getBody().toString()));
        ObjectMapper objectMapper = new ObjectMapper();
        List<StatDto> allStatsList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });

        Map<Long, Long> statMap = new HashMap<>();

        statMap = allStatsList.stream()
                .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                .collect(Collectors.toMap(
                        statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())),
                        StatDto::getHits
                ));
        log.info(String.format("Map stat - id : %s \n", statMap.toString()));
        return statMap;
    }

}
