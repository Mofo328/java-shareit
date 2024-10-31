package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDtoCreated;

    private ItemBookingInfoDto itemBookingInfoDtoCreated;

    private CommentDto commentDtoCreated;

    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() {
        itemDtoCreated = new ItemDto();
        itemDtoCreated.setId(1L);
        itemDtoCreated.setName("firstName");
        itemDtoCreated.setDescription("description");
        itemDtoCreated.setAvailable(true);
        itemDtoCreated.setRequestId(1L);

        commentDtoCreated = new CommentDto();
        commentDtoCreated.setId(1L);
        commentDtoCreated.setText("text");
        commentDtoCreated.setAuthorName("author");
        commentDtoCreated.setCreated(LocalDateTime.now());

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1L));
        bookingDto.setItemId(1L);
        bookingDto.setBooker(1L);
        bookingDto.setStatus(BookingStatus.APPROVED);

        itemBookingInfoDtoCreated = new ItemBookingInfoDto();
        itemBookingInfoDtoCreated.setId(1L);
        itemBookingInfoDtoCreated.setName("name");
        itemBookingInfoDtoCreated.setLastBooking(bookingDto);
        itemBookingInfoDtoCreated.setNextBooking(bookingDto);
        itemBookingInfoDtoCreated.setComments(List.of(commentDtoCreated));
        itemBookingInfoDtoCreated.setAvailable(true);
    }

    @AfterEach
    void tearDown() {
        itemDtoCreated = null;
        itemBookingInfoDtoCreated = null;
        commentDtoCreated = null;
        bookingDto = null;
    }

    @Test
    void create() throws Exception {
        when(itemService.create(1L, itemDtoCreated))
                .thenReturn(itemDtoCreated);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoCreated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoCreated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoCreated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoCreated.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoCreated.getRequestId().intValue())));
    }

    @Test
    void update() throws Exception {
        when(itemService.update(1L, 1L, itemDtoCreated))
                .thenReturn(itemDtoCreated);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDtoCreated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoCreated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoCreated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoCreated.getAvailable())));
    }

    @Test
    void getById() throws Exception {
        when(itemService.get(1L))
                .thenReturn(itemBookingInfoDtoCreated);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemBookingInfoDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemBookingInfoDtoCreated.getName())))
                .andExpect(jsonPath("$.description", is(itemBookingInfoDtoCreated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemBookingInfoDtoCreated.getAvailable())))
                .andExpect(jsonPath("$.comments[0].text", is(itemBookingInfoDtoCreated.getComments().get(0).getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(itemBookingInfoDtoCreated.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(itemBookingInfoDtoCreated.getComments().get(0).getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.lastBooking.id", is(itemBookingInfoDtoCreated.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$.lastBooking.start", is(itemBookingInfoDtoCreated.getLastBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.lastBooking.end", is(itemBookingInfoDtoCreated.getLastBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.lastBooking.status", is(itemBookingInfoDtoCreated.getLastBooking().getStatus().toString())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemBookingInfoDtoCreated.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$.nextBooking.start", is(itemBookingInfoDtoCreated.getNextBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.nextBooking.end", is(itemBookingInfoDtoCreated.getNextBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.nextBooking.status", is(itemBookingInfoDtoCreated.getNextBooking().getStatus().toString())));
    }

    @Test
    void getOwnerItems() throws Exception {
        when(itemService.getOwnerItems(1L))
                .thenReturn(List.of(itemBookingInfoDtoCreated));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemBookingInfoDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemBookingInfoDtoCreated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemBookingInfoDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemBookingInfoDtoCreated.getAvailable())))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemBookingInfoDtoCreated.getComments().get(0).getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(itemBookingInfoDtoCreated.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created", is(itemBookingInfoDtoCreated.getComments().get(0).getCreated().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemBookingInfoDtoCreated.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].lastBooking.start", is(itemBookingInfoDtoCreated.getLastBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].lastBooking.end", is(itemBookingInfoDtoCreated.getLastBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].lastBooking.status", is(itemBookingInfoDtoCreated.getLastBooking().getStatus().toString())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemBookingInfoDtoCreated.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].nextBooking.start", is(itemBookingInfoDtoCreated.getNextBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].nextBooking.end", is(itemBookingInfoDtoCreated.getNextBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].nextBooking.status", is(itemBookingInfoDtoCreated.getNextBooking().getStatus().toString())));
    }

    @Test
    void search() throws Exception {
        when(itemService.search("search"))
                .thenReturn(List.of(itemDtoCreated));

        mvc.perform(get("/items/search?text=search")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoCreated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoCreated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoCreated.getAvailable())));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(1L, 1L, commentDtoCreated))
                .thenReturn(commentDtoCreated);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoCreated))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoCreated.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoCreated.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoCreated.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(commentDtoCreated.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));
    }
}