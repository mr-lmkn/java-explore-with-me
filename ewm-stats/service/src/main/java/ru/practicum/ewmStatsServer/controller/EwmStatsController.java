package ru.practicum.ewmStatsServer.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmStatsDto.HitInDto;
import ru.practicum.ewmStatsDto.HitOutDto;
import ru.practicum.ewmStatsDto.StatDto;
import ru.practicum.ewmStatsServer.service.EwmStatsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
public class EwmStatsController {
    EwmStatsService ewmStatsService;

    @PostMapping(path = "/hit", consumes = "application/json;charset=UTF-8", produces = "application/json;")
    public HitOutDto hit(@RequestBody @Validated HitInDto hitInDto) {
        log.info("Got Hit save request: {}", hitInDto);
        return ewmStatsService.saveHit(hitInDto);
    }

    @GetMapping("/stats")
    public List<StatDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        log.info("GET request to get statistic: from = {}, to = {}, urisList = {}, uniqueIp = {}", start, end, uris, unique);
        Optional urisOpt = Optional.ofNullable(uris);
        return ewmStatsService.getStats(start, end, urisOpt, unique);
    }
}
