package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.UserNotOwnerException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryAndServiceTest {

    @Autowired
    private final ItemServiceImpl itemService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

    @Test
    void create() {
        User owner = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();

        when(itemRepository.save(any()))
                .thenReturn(item);

        itemDto = itemService.create(1L, itemDto);

        assertThat(itemDto, is(notNullValue()));
    }

    @Test
    void throwUserNotFoundException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        NotFoundException invalidUserIdException;

        UserNotOwnerException userNotOwnerException;

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.create(1L, itemDto));
        assertThat(invalidUserIdException.getMessage(), is("Пользователя нет"));

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(1L, 1L, itemDto));
        assertThat(invalidUserIdException.getMessage(), is("Пользователя нет"));

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.get(2L));
        assertThat(invalidUserIdException.getMessage(), is("Вещи нет"));

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getOwnerItems(3L));
        assertThat(invalidUserIdException.getMessage(), is("Пользователя нет"));

        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@email.com")
                .build();
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(3L))
                .thenReturn(Optional.empty());

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addComment(3L, 1L, commentDto));
        assertThat(invalidUserIdException.getMessage(), is("Пользователя нет"));
    }

    @Test
    void throwItemNotFoundException() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("name")
                .email("user2@email.com")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        NotFoundException notFoundException;

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(1L, 1L, itemDto));
        assertThat(notFoundException.getMessage(), is("Вещи нет"));

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.get(1L));
        assertThat(notFoundException.getMessage(), is("Вещи нет"));

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(1L, 3L, itemDto));
        assertThat(notFoundException.getMessage(), is("Вещи нет"));

        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        notFoundException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addComment(3L, 1L, commentDto));
        assertThat(notFoundException.getMessage(), is("Пользователя нет"));
    }


    @Test
    void update() throws Exception {
        User owner = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(owner));

        ItemDto itemDto = ItemDto.builder()
                .name("nameUpdated")
                .description("descriptionUpdated")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Item itemUpdated = Item.builder()
                .id(1L)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.save(any()))
                .thenReturn(itemUpdated);

        itemDto = itemService.update(1L, 1L, itemDto);
        assertThat(itemDto, is(notNullValue()));
    }

    @Test
    void getItem() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.com")
                .build();

        User owner = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@email.com")
                .build();

        User booker = User.builder()
                .id(3L)
                .name("user3")
                .email("user3@email.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(booker)
                .created(created)
                .build();
        List<Comment> commentList = List.of(comment);

        ItemBookingInfoDto itemDto;

        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(created.plusDays(1L))
                .end(created.plusDays(2L))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(1L))
                .thenReturn(commentList);

        itemDto = itemService.get(1L);
        assertThat(itemDto, is(notNullValue()));

        itemDto = itemService.get(1L);
        assertThat(itemDto.getLastBooking(), is(nullValue()));
        assertThat(itemDto.getNextBooking(), is(nullValue()));
    }


    @Test
    void search() {
        User owner = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@email.com")
                .build();

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(owner));

        List<ItemDto> itemDtos = itemService.search("dsfsdfsdf");
        Assertions.assertTrue(itemDtos.isEmpty());

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.searchAvailableByText(any()))
                .thenReturn(Collections.emptyList());

        itemDtos = itemService.search("text");
        Assertions.assertTrue(itemDtos.isEmpty());

        List<Item> items = List.of(item);

        when(itemRepository.searchAvailableByText(any()))
                .thenReturn(items);
        itemDtos = itemService.search("description");
        assertThat(itemDtos, is(notNullValue()));
    }
}