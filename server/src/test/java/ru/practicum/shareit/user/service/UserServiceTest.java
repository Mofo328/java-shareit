package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.maper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    @Autowired
    private final UserService userService;

    @Autowired
    private final UserRepository userRepository;

    private User userCreate;
    private User userUpdate;

    @BeforeEach
    void setUp() {
        userCreate = new User();
        userCreate.setName("Test User");
        userCreate.setEmail("testuser@example.com");
        userRepository.save(userCreate);

        userUpdate = new User();
        userUpdate.setName("Update User2");
        userUpdate.setEmail("Updateuser2@example.com");
        userRepository.save(userUpdate);
    }

    @Test
    void create() {
        UserDto userDto = new UserDto();
        userDto.setEmail("email@yandex.ru");
        userDto.setName("Name");
        UserDto userCreated = userService.create(userDto);
        assertThat(userCreated).isNotNull();
        assertThat(userCreated.getName()).isEqualTo("Name");
        assertThat(userCreated.getEmail()).isEqualTo("email@yandex.ru");
    }

    @Test
    void update() {
        UserDto userDto = new UserDto();
        userDto.setEmail("email@yandex.ru");
        userDto.setName("Name");
        UserDto userAfterUpdate = userService.update(userUpdate.getId(), userDto);
        assertThat(userAfterUpdate).isNotNull();
        assertThat(userAfterUpdate.getName()).isEqualTo("Name");
        assertThat(userAfterUpdate.getId()).isEqualTo(userUpdate.getId());
        assertThat(userAfterUpdate.getEmail()).isEqualTo("email@yandex.ru");
    }

    @Test
    void failNotExistUserUpdate() {
        UserDto userDto = new UserDto();
        userDto.setEmail("email@yandex.ru");
        userDto.setName("Name");
        assertThrows(NotFoundException.class, () ->
                userService.update(4L, userDto));
    }

    @Test
    void get() {
        UserDto userGet = userService.get(userCreate.getId());
        assertThat(userGet).isNotNull();
        assertThat(userGet.getName()).isEqualTo(userCreate.getName());
        assertThat(userGet.getId()).isEqualTo(userCreate.getId());
        assertThat(userGet.getEmail()).isEqualTo(userCreate.getEmail());
    }

    @Test
    void getAll() {
        List<UserDto> users = userService.getAll();
        assertThat(users).isNotNull();
        assertThat(users).isEqualTo(List.of(UserMapper.toUserDto(userCreate), UserMapper.toUserDto(userUpdate)));
    }

    @Test
    void failNotExistUserGet() {
        assertThrows(NotFoundException.class, () ->
                userService.get(4L));
    }

    @Test
    void delete() {
        userService.delete(userUpdate.getId());
        assertThrows(NotFoundException.class, () ->
                userService.get(userUpdate.getId()));
    }
}