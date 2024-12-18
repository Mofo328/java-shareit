package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Получены данные на создание пользователя {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Получены данные на обновление: {}; пользователя c ID {}", userDto, id);
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя по ID {}", id);
        return userService.get(id);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получили запрос на удаление пользователя по ID {}", id);
        userService.delete(id);
    }
}
