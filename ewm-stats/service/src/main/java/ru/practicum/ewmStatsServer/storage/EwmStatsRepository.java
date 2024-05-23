package ru.practicum.ewmStatsServer.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewmStatsServer.model.Hit;

@EnableJpaRepositories
public interface EwmStatsRepository extends JpaRepository<Hit, Long> {
}
