package ru.practicum.ewmServer.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmServer.error.exceptions.NotFoundException;
import ru.practicum.ewmServer.users.dto.NewUserRequestDto;
import ru.practicum.ewmServer.users.dto.UserDto;
import ru.practicum.ewmServer.users.model.UserModel;
import ru.practicum.ewmServer.users.storage.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<UserModel> usersList;
        usersList = (ids != null) ? usersRepository.findAllByIdIn(ids, pageRequest)
                : usersRepository.findAll(pageRequest).toList();
        log.info(String.format("Got users:  %s", usersList));
        return usersList.stream()
                .map(p -> modelMapper.map(p, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(NewUserRequestDto newUser) {
        UserModel newUserModel = modelMapper.map(newUser, UserModel.class);
        UserModel saved = usersRepository.save(newUserModel);
        return modelMapper.map(saved, UserDto.class);
    }

    @Override
    public void delete(Long id) throws NotFoundException {
        getUser(id);
        usersRepository.deleteById(id);
    }

    @Override
    public UserModel getUser(Long id) throws NotFoundException {
        Optional<UserModel> user = usersRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        String msg = String.format("User 'id' %s. is not exists", id);
        log.info(msg);
        throw new NotFoundException(msg);
    }
}
