package ru.practicum.ewmServer.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.compilations.dto.CompilationDto;
import ru.practicum.ewmServer.compilations.service.CompilationsService;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationsController {
    private final CompilationsService compilationsService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("getAllCompilations request required = {}, from = {}, size = {}", pinned, from, size);
        return compilationsService.publicGetAll(pinned, from, size);
    }

    @GetMapping("/{id}")
    public CompilationDto findCompilationById(@PathVariable Long id) throws NotFoundException {
        log.info("findCompilationById request id = {}", id);
        return compilationsService.publicGetById(id);
    }
}
