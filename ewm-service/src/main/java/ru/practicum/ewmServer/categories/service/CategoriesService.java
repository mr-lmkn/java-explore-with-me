package ru.practicum.ewmServer.categories.service;

import ru.practicum.ewmServer.categories.dto.CategoryDto;
import ru.practicum.ewmServer.categories.dto.OutCategoryDto;
import ru.practicum.ewmServer.categories.model.CategoryModel;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

public interface CategoriesService {
    CategoryDto create(OutCategoryDto outCategoryDto) throws ConstraintViolationException;

    void delete(Long id) throws NotFoundException, ConflictException;

    CategoryDto update(Long id, OutCategoryDto outCategoryDto) throws NotFoundException;

    List<CategoryDto> publicGetAll(Integer from, Integer size);

    CategoryDto publicGetById(Long id) throws NotFoundException;

    CategoryModel getCategory(Long id) throws NotFoundException;

}
