package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Создали нового пользователя={}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Обновили пользователя={}", userDto);
        return userClient.updateUser(id, userDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Пользователь по id={}", id);
        return userClient.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> retrieveAllUsers() {
        log.info("Получить всех пользователей");
        return userClient.getUsers();
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable Long id) {
        log.info("Удалить пользователя с Id= {}", id);
        userClient.removeUser(id);
    }

}