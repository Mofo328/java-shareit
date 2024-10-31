package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    MockMvc mvc;

    ItemRequestDto itemRequestDtoCreated;

    ItemDto itemDtoCreated;

    @BeforeEach
    void setUp() {
        itemDtoCreated = new ItemDto();
        itemDtoCreated.setId(1L);
        itemDtoCreated.setName("name");
        itemDtoCreated.setDescription("description");
        itemDtoCreated.setRequestId(1L);

        itemRequestDtoCreated = new ItemRequestDto();
        itemRequestDtoCreated.setId(1L);
        itemRequestDtoCreated.setDescription("description");
        itemRequestDtoCreated.setCreated(LocalDateTime.now());
        itemRequestDtoCreated.setItems(List.of(itemDtoCreated));
    }

    @AfterEach
    void tearDown() {
        itemRequestDtoCreated = null;

        itemDtoCreated = null;
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(1L, itemRequestDtoCreated))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoCreated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDtoCreated.getItems().get(0).getRequestId().intValue())))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDtoCreated.getItems().get(0).getDescription())));

    }

    @Test
    void getAllRequestsForUser() throws Exception {
        when(itemRequestService.getAllRequestsForUser(1L))
                .thenReturn(List.of(itemRequestDtoCreated));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())));
    }

    @Test
    void getAll() throws Exception {
        when(itemRequestService.getAllRequests(1L))
                .thenReturn(List.of(itemRequestDtoCreated));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 1L))
                .thenReturn(itemRequestDtoCreated);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoCreated.getDescription())))
                .andExpect(jsonPath("$.created",
                        is(itemRequestDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.items[0].id",
                        is(itemRequestDtoCreated.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name",
                        is(itemRequestDtoCreated.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description",
                        is(itemRequestDtoCreated.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available",
                        is(itemRequestDtoCreated.getItems().get(0).getAvailable())));
    }
}