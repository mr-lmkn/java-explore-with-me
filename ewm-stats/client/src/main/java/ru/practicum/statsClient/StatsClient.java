package ru.practicum.statsClient;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewmStatsDto.Constants;
import ru.practicum.ewmStatsDto.HitInDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StatsClient extends BaseClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    public StatsClient(String StatsServiceUrl, RestTemplateBuilder builder) {

        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(StatsServiceUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(String applicationName, String uri, String ip) {
        final HitInDto hit = HitInDto.builder()
                .app(applicationName)
                .uri(uri)
                .ip(ip)
                .timestamp(Timestamp.from(Instant.now()).toLocalDateTime())
                .build();
        String path = "/hit";
        return post(path, hit);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end
            , Optional<List<String>> uris, Boolean unique) {
        StringBuilder uriBuilder = new StringBuilder("/stats?start={start}&end={end}");
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter)
        ));

        if (uris.isPresent()) {
            parameters.put("uris", String.join(",", uris.get()));
            uriBuilder.append("&uris={uris}".repeat(uris.get().size()));
        }
        if (unique) {
            parameters.put("unique", true);
            uriBuilder.append("&unique={unique}");
        }
        return get(uriBuilder.toString(), parameters);
    }
}