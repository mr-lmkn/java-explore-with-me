package ru.practicum.ewmServer.compilations.service;

import ru.practicum.ewmServer.compilations.dto.CompilationDto;
import ru.practicum.ewmServer.compilations.dto.NewCompilationDto;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;

import java.util.List;

public interface CompilationsService {
    CompilationDto create(NewCompilationDto compilationDto);

    CompilationDto update(Long id, NewCompilationDto update) throws NotFoundException;

    void delete(Long id) throws NotFoundException;

    List<CompilationDto> publicGetAll(Boolean pinned, Integer from, Integer size);

    CompilationDto publicGetById(Long id) throws NotFoundException;
}
