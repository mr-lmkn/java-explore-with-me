package ru.practicum.ewmServer.stats.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.stats.service.StatServiceImpl;
import ru.practicum.ewmStatsDto.HitInDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
public class StatsController {
    private StatServiceImpl statTest;

    @PostMapping(path = "/hit", consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public void hit(@RequestBody @Validated HitInDto hitInDto) {
        log.info("Got Hit save request: {}", hitInDto);
        statTest.save(hitInDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET request to get statistic: from = {}, to = {}, urisList = {}, uniqueIp = {}",
                start, end, uris, unique);
        Optional urisOpt = Optional.ofNullable(uris);
        return statTest.get(start, end, urisOpt, unique);
    }
}
