package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDtoCreated;

    @BeforeEach
    void setUp() {
        userDtoCreated = new UserDto();
        userDtoCreated.setId(1L);
        userDtoCreated.setName("name");
        userDtoCreated.setEmail("email@mail.ru");
    }

    @AfterEach
    void tearDown() {
        userDtoCreated = null;
    }

    @Test
    void create() throws Exception {
        when(userService.create(userDtoCreated))
                .thenReturn(userDtoCreated);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoCreated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoCreated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoCreated.getEmail())));
    }

    @Test
    void update() throws Exception {
        when(userService.update(1L, userDtoCreated))
                .thenReturn(userDtoCreated);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoCreated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoCreated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoCreated.getEmail())));
    }

    @Test
    void getUserDto() throws Exception {
        when(userService.get(1L))
                .thenReturn(userDtoCreated);
        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoCreated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoCreated.getEmail())));
    }

    @Test
    void deleteUserDto() throws Exception {
        mvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService).delete(1L);
    }

    @Test
    void getAll() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDtoCreated));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoCreated.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoCreated.getEmail())));
    }
}