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
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDtoUpdate) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователя нет"));
        if (userDtoUpdate.getName() != null) {
            user.setName(userDtoUpdate.getName());
        }
        if (userDtoUpdate.getEmail() != null) {
            user.setEmail(userDtoUpdate.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto get(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователя нет")
        );
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

}
