package ru.practicum.ewmServer.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.compilations.dto.CompilationDto;
import ru.practicum.ewmServer.compilations.dto.NewCompilationDto;
import ru.practicum.ewmServer.compilations.service.CompilationsService;
import ru.practicum.ewmServer.dtoValidateGroups.GroupCreate;
import ru.practicum.ewmServer.dtoValidateGroups.GroupUpdate;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationsAdminController {
    private final CompilationsService compilationsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(
            @RequestBody @Validated(GroupCreate.class) NewCompilationDto compilationDto
    ) {
        log.info("Add compilation request {} (Admin)", compilationDto);
        return compilationsService.create(compilationDto);
    }

    @PatchMapping("/{id}")
    public CompilationDto update(
            @PathVariable Long id,
            @RequestBody @Validated({GroupUpdate.class,}) NewCompilationDto update
    ) throws NotFoundException {
        log.info("Update compilation id = {} request {} (Admin)", id, update);
        return compilationsService.update(id, update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        log.info("Delete compilation request (Admin)");
        compilationsService.delete(id);
    }
}
