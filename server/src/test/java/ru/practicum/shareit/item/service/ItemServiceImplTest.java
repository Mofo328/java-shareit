package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    private User owner;
    private User user;

    private Item item;
    private Item item2;

    private Booking booking;
    private Booking booking2;
    private Booking booking3;

    private ItemRequest itemRequest;

    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Test User");
        owner.setEmail("testuser@example.com");
        userRepository.save(owner);

        user = new User();
        user.setName("Test User2");
        user.setEmail("testuser2@example.com");
        userRepository.save(user);

        itemRequest = new ItemRequest();
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Description");
        itemRequestRepository.save(itemRequest);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.of(2024, 10, 30, 20, 0, 0));
        booking.setEnd(LocalDateTime.of(2024, 11, 4, 21, 0, 0));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        item2 = new Item();
        item2.setName("Test Item2");
        item2.setDescription("Description2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2.setRequest(itemRequest);
        itemRepository.save(item2);

        booking2 = new Booking();
        booking2.setStart(LocalDateTime.of(2024, 06, 4, 20, 0, 0));
        booking2.setEnd(LocalDateTime.of(2024, 06, 4, 21, 0, 0));
        booking2.setItem(item);
        booking2.setBooker(user);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        booking3 = new Booking();
        booking3.setStart(LocalDateTime.of(2024, 12, 4, 20, 0, 0));
        booking3.setEnd(LocalDateTime.of(2024, 12, 4, 21, 0, 0));
        booking3.setItem(item);
        booking3.setBooker(user);
        booking3.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking3);

        comment = new Comment();
        comment.setAuthor(user);
        comment.setText("Text");
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        commentRepository.save(comment);
    }

    @Test
    void createByItemRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(3L);
        itemDto.setName("name3");
        itemDto.setDescription("something");
        itemDto.setAvailable(true);
        itemDto.setRequestId(itemRequest.getId());
        ItemDto createItem = itemService.create(owner.getId(), itemDto);
        assertThat(createItem).isNotNull();
        assertThat(createItem.getName()).isEqualTo("name3");
        assertThat(createItem.getDescription()).isEqualTo("something");
        assertThat(createItem.getAvailable()).isEqualTo(true);
        assertThat(createItem.getRequestId()).isEqualTo(itemRequest.getId());
    }

    @Test
    void create() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(3L);
        itemDto.setName("name3");
        itemDto.setDescription("something");
        itemDto.setAvailable(true);
        ItemDto createItem = itemService.create(owner.getId(), itemDto);
        assertThat(createItem).isNotNull();
        assertThat(createItem.getName()).isEqualTo("name3");
        assertThat(createItem.getDescription()).isEqualTo("something");
        assertThat(createItem.getAvailable()).isEqualTo(true);
    }

    @Test
    void failUserNotExistCreate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(3L);
        itemDto.setName("name3");
        itemDto.setDescription("something");
        itemDto.setAvailable(true);
        assertThrows(NotFoundException.class, () ->
                itemService.create(4L, itemDto));
    }

    @Test
    void failItemRequestNotExistCreate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(3L);
        itemDto.setName("name3");
        itemDto.setDescription("something");
        itemDto.setAvailable(true);
        itemDto.setRequestId(4L);
        assertThrows(NotFoundException.class, () ->
                itemService.create(4L, itemDto));
    }

    @Test
    void get() {
        ItemBookingInfoDto itemBookingInfoDto = itemService.get(item.getId());
        assertThat(itemBookingInfoDto).isNotNull();
        assertThat(itemBookingInfoDto.getName()).isEqualTo(item.getName());
        assertThat(itemBookingInfoDto.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemBookingInfoDto.getAvailable()).isEqualTo(true);
        assertThat(itemBookingInfoDto.getLastBooking()).isEqualTo(BookingMapper.toBookingDto(booking3));
        assertThat(itemBookingInfoDto.getNextBooking()).isEqualTo(BookingMapper.toBookingDto(booking3));
        CommentDto commentDto = itemBookingInfoDto.getComments().get(0);
        System.out.println(commentDto);
        assertThat(itemBookingInfoDto.getComments()).isEqualTo(List.of(CommentMapper.toCommentDto(comment)));
    }

    @Test
    void failItemNotExistGet() {
        assertThrows(NotFoundException.class, () ->
                itemService.get(6L));
    }

    @Test
    void update() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name3");
        itemDto.setDescription("something");
        itemDto.setAvailable(true);
        ItemDto createItem = itemService.update(owner.getId(), item.getId(), itemDto);
        assertThat(createItem).isNotNull();
        assertThat(createItem.getName()).isEqualTo("name3");
        assertThat(createItem.getDescription()).isEqualTo("something");
        assertThat(createItem.getAvailable()).isEqualTo(true);
    }

    @Test
    void failItemNotExistUpdate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(3L);
        itemDto.setName("name3");
        itemDto.setDescription("something");
        itemDto.setAvailable(true);
        assertThrows(NotFoundException.class, () ->
                itemService.update(owner.getId(), 5L, itemDto));
    }

    @Test
    void failUserNotExistUpdate() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name3");
        itemDto.setDescription("something");
        itemDto.setAvailable(true);
        assertThrows(NotFoundException.class, () ->
                itemService.update(6L, 1L, itemDto));
    }

    @Test
    void getOwnerItems() {
        List<ItemBookingInfoDto> bookingInfoDto = itemService.getOwnerItems(owner.getId());
        ItemBookingInfoDto itemBookingInfoDto1 = itemService.get(item.getId());
        ItemBookingInfoDto itemBookingInfoDto2 = itemService.get(item2.getId());
        assertThat(bookingInfoDto).isEqualTo(List.of(itemBookingInfoDto1, itemBookingInfoDto2));
    }

    @Test
    void addComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(2L);
        commentDto.setText("text2");
        commentDto.setAuthorName("AuthorName");
        commentDto.setCreated(LocalDateTime.now());
        CommentDto commentCreated = itemService.addComment(user.getId(), item.getId(), commentDto);
        assertThat(commentCreated).isNotNull();
        assertThat(commentCreated.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentCreated.getText()).isEqualTo("text2");
        assertThat(commentCreated.getId()).isEqualTo(commentDto.getId());
    }

    @Test
    void failNotBookingExistAddComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(2L);
        commentDto.setText("text2");
        commentDto.setAuthorName("AuthorName");
        commentDto.setCreated(LocalDateTime.now());
        assertThrows(BadRequestException.class, () ->
                itemService.addComment(owner.getId(), item2.getId(), commentDto));
    }

    @Test
    void failNotUserExistAddComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(2L);
        commentDto.setText("text2");
        commentDto.setAuthorName("AuthorName");
        commentDto.setCreated(LocalDateTime.now());
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(3L, item2.getId(), commentDto));
    }

    @Test
    void failNotItemExistAddComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(2L);
        commentDto.setText("text2");
        commentDto.setAuthorName("AuthorName");
        commentDto.setCreated(LocalDateTime.now());
        assertThrows(NotFoundException.class, () ->
                itemService.addComment(owner.getId(), 3L, commentDto));
    }

    @Test
    void search() {
        List<ItemDto> searchItems = itemService.search("Description");
        assertThat(searchItems).isEqualTo(List.of(ItemMapper.itemDto(item), ItemMapper.itemDto(item2)));
    }

    @Test
    void searchWithBlankQuery() {
        List<ItemDto> searchItems = itemService.search("");
        assertThat(searchItems).isEqualTo(Collections.emptyList());
    }
}