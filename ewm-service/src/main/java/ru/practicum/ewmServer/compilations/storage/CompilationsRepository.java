package ru.practicum.ewmServer.compilations.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewmServer.compilations.model.CompilationModel;

@EnableJpaRepositories
public interface CompilationsRepository extends JpaRepository<CompilationModel, Long> {
}
