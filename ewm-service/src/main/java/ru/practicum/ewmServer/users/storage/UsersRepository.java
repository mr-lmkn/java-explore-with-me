package ru.practicum.ewmServer.users.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.practicum.ewmServer.users.model.UserModel;

import java.util.List;

@EnableJpaRepositories
public interface UsersRepository extends JpaRepository<UserModel, Long> {
    List<UserModel> findAllByIdIn(List<Long> ids, PageRequest pageRequest);
}
