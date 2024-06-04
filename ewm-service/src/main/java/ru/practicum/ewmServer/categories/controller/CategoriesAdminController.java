package ru.practicum.ewmServer.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.categories.dto.CategoryDto;
import ru.practicum.ewmServer.categories.dto.OutCategoryDto;
import ru.practicum.ewmServer.categories.service.CategoriesService;
import ru.practicum.ewmServer.error.exceptions.ConflictException;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoriesAdminController {
    private final CategoriesService categoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Validated OutCategoryDto outCategoryDto)
            throws ConstraintViolationException {
        log.info("createCategory request {} (Admin)", outCategoryDto);
        return categoriesService.create(outCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable @Positive Long catId
    ) throws NotFoundException, ConflictException {
        log.info("deleteCategory request id={} (Admin)", catId);
        categoriesService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(
            @PathVariable @Positive Long catId,
            @RequestBody @Validated OutCategoryDto outCategoryDto
    ) throws NotFoundException {
        log.info("updateCategory request id={} (Admin)", catId);
        return categoriesService.update(catId, outCategoryDto);
    }
}
