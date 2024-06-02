package ru.practicum.ewmServer.categories.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewmServer.categories.model.CategoryModel;

@EnableJpaRepositories
public interface CategoriesRepository extends JpaRepository<CategoryModel, Long> {
}
