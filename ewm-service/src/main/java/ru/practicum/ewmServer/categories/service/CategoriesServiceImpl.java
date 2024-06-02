package ru.practicum.ewmServer.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmServer.categories.dto.CategoryDto;
import ru.practicum.ewmServer.categories.dto.OutCategoryDto;
import ru.practicum.ewmServer.categories.model.CategoryModel;
import ru.practicum.ewmServer.categories.storage.CategoriesRepository;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.events.storage.EventsRepository;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventsRepository eventsRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CategoryDto create(OutCategoryDto outCategoryDto) throws ConstraintViolationException {
        CategoryModel categoryModel = modelMapper.map(outCategoryDto, CategoryModel.class);
        categoryModel = categoriesRepository.save(categoryModel);
        CategoryDto ret = modelMapper.map(categoryModel, CategoryDto.class);
        log.info("Ok {}", ret);
        return ret;
    }

    @Override
    public void delete(Long id) throws NotFoundException, ConflictException {
        getCategory(id);
        if (eventsRepository.findByCategoryId(id).isPresent()) {
            throw new ConflictException("Can't delete, is used by some Events");
        }
        categoriesRepository.deleteById(id);
        log.info("Ok ");
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, OutCategoryDto outCategoryDto) throws NotFoundException {
        CategoryModel categoryModel = getCategory(id);
        categoryModel.setName(outCategoryDto.getName());
        categoryModel = categoriesRepository.save(categoryModel);
        CategoryDto ret = modelMapper.map(categoryModel, CategoryDto.class);
        log.info("Ok {}", ret);
        return ret;
    }

    @Override
    public List<CategoryDto> publicGetAll(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<CategoryDto> ret = categoriesRepository.findAll(pageRequest).stream()
                .map(p -> modelMapper.map(p, CategoryDto.class))
                .collect(Collectors.toList());
        log.info("Ok {}", ret);
        return ret;
    }

    @Override
    public CategoryDto publicGetById(Long id) throws NotFoundException {
        CategoryDto ret = modelMapper.map(getCategory(id), CategoryDto.class);
        log.info("Ok");
        return ret;
    }

    public CategoryModel getCategory(Long id) throws NotFoundException {
        CategoryModel ret = categoriesRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Category 'id' %s. is not exists", id)));
        log.info("Ok {}", ret);
        return ret;
    }

}
