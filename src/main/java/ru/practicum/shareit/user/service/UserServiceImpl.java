package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDtoUpdate) {
        User user = UserMapper.toUser(userDtoUpdate);
        user.setId(id);
        return UserMapper.toUserDto(userRepository.update(user));
    }

    @Override
    public UserDto get(Long id) {
        User user = userRepository.get(id).orElseThrow(() ->
                new NotFoundException("Пользователя нет")
        );
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

}
