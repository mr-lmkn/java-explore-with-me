package ru.practicum.ewmServer.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmServer.categories.dto.CategoryDto;
import ru.practicum.ewmServer.categories.service.CategoriesService;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoriesService categoriesService;

    @GetMapping
    public List<CategoryDto> publicGetAllCategories(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("getAll Categories request from = {}, size = {}", from, size);
        return categoriesService.publicGetAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto publicGetByCategoryId(@PathVariable Long catId) throws NotFoundException {
        log.info("publicGetByCategoryId Get request id = {}", catId);
        return categoriesService.publicGetById(catId);
    }
}
