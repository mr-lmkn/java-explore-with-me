package ru.practicum.ewmServer.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmServer.compilations.dto.CompilationDto;
import ru.practicum.ewmServer.compilations.dto.NewCompilationDto;
import ru.practicum.ewmServer.compilations.model.CompilationModel;
import ru.practicum.ewmServer.compilations.storage.CompilationsRepository;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.model.EventModel;
import ru.practicum.ewmServer.events.storage.EventsRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final EventsRepository eventsRepository;
    private final ModelMapper modelMapper;

    /**
     * <B>ADMIN</B>
     * Добавление новой подборки (подборка может не содержать событий)
     *
     * @param compilationDto
     * @return
     */
    @Override
    public CompilationDto create(NewCompilationDto compilationDto) {
        CompilationModel newCompilation = modelMapper.map(compilationDto, CompilationModel.class);
        addEventsByIds(newCompilation, compilationDto);
        CompilationModel saved = compilationsRepository.save(newCompilation);
        return modelMapper.map(saved, CompilationDto.class);
    }

    /**
     * <B>ADMIN</B>
     *
     * @param id
     * @param updateCompilation
     * @return
     * @throws NotFoundException
     */
    @Override
    public CompilationDto update(Long id, NewCompilationDto updateCompilation) throws NotFoundException {
        CompilationModel compilation = getCompilation(id);
        compilation.setPinned(updateCompilation.getPinned());
        addEventsByIds(compilation, updateCompilation);
        CompilationModel saved = compilationsRepository.save(compilation);
        return modelMapper.map(saved, CompilationDto.class);
    }

    /**
     * <B>ADMIN</B>
     *
     * @param id
     * @throws NotFoundException
     */
    @Override
    public void delete(Long id) throws NotFoundException {
        getCompilation(id);
        compilationsRepository.deleteById(id);
    }

    /**
     * <B>PUBLIC: получение списка подборок<B/>
     * <p></p>В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список</p>
     *
     * @param pinned
     * @param from
     * @param size
     * @return
     */
    @Override
    public List<CompilationDto> publicGetAll(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return compilationsRepository.findAll(pageRequest).stream()
                .map(p -> modelMapper.map(p, CompilationDto.class))
                .collect(Collectors.toList());
    }

    /**
     * <B>PUBLIC: Получение подборки событий по его id</B>
     * <P>В случае, если подборки с заданным id не найдено, возвращает статус код 404</P>
     *
     * @param id
     * @return
     * @throws NotFoundException
     */
    @Override
    public CompilationDto publicGetById(Long id) throws NotFoundException {
        CompilationModel compilation = getCompilation(id);
        return modelMapper.map(compilation, CompilationDto.class);
    }

    private CompilationModel getCompilation(Long id) throws NotFoundException {
        return compilationsRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Compilation 'id' %s. is not exists", id)));
    }

    private CompilationModel addEventsByIds(CompilationModel model, NewCompilationDto dto) {
        if (!Objects.isNull(dto.getEvents())) {
            List<EventModel> affectedEvents = eventsRepository.findAllById(dto.getEvents());
            model.setEvents(affectedEvents);
        }
        return model;
    }

}
