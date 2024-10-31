package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.error.AvailableException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.UserNotOwnerException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    private User user;
    private User user2;
    private Item item;
    private Item item2;

    private Booking booking;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        userRepository.save(user);

        user2 = new User();
        user2.setName("Test User2");
        user2.setEmail("testuser2@example.com");
        userRepository.save(user2);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user2);
        itemRepository.save(item);

        item2 = new Item();
        item2.setName("Test Item");
        item2.setDescription("Description");
        item2.setAvailable(true);
        item2.setOwner(user2);
        itemRepository.save(item2);

        booking = new Booking();
        booking.setStart(LocalDateTime.of(2024, 10, 30, 20, 0, 0));
        booking.setEnd(LocalDateTime.of(2024, 12, 4, 21, 0, 0));
        booking.setItem(item);
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

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
    }

    @Test
    void create() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item2.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingInfoDto createdBooking = bookingService.create(user.getId(), bookingDto);
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(createdBooking.getItem().getId()).isEqualTo(item2.getId());
        assertThat(createdBooking.getBooker().getId()).isEqualTo(user.getId());
    }

    @Test
    void failItemNotExistCreate() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(4L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(NotFoundException.class, () ->
                bookingService.create(1L, bookingDto));
    }

    @Test
    void failUserNotExistCreate() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(NotFoundException.class, () ->
                bookingService.create(5L, bookingDto));
    }

    @Test
    void failItemAvailableCreate() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        assertThrows(AvailableException.class, () ->
                bookingService.create(user.getId(), bookingDto));
    }

    @Test
    void approve() {
        BookingInfoDto approved = bookingService.approve(user2.getId(), booking.getId(), true);
        assertThat(approved).isNotNull();
        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(approved.getItem().getId()).isEqualTo(item.getId());
        assertThat(approved.getBooker().getId()).isEqualTo(user2.getId());
    }

    @Test
    void rejected() {
        BookingInfoDto approved = bookingService.approve(user2.getId(), booking.getId(), false);
        assertThat(approved).isNotNull();
        assertThat(approved.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(approved.getItem().getId()).isEqualTo(item.getId());
        assertThat(approved.getBooker().getId()).isEqualTo(user2.getId());
        assertThat(approved.getStart().isEqual(LocalDateTime.of(2024, 10, 30, 20, 0, 0)));
        assertThat(approved.getEnd().isEqual(LocalDateTime.of(2024, 12, 4, 21, 0, 0)));
    }

    @Test
    void approveUserNotOwner() {
        assertThrows(UserNotOwnerException.class, () ->
                bookingService.approve(5L, booking.getId(), true));
    }

    @Test
    void approveCannotChangeStatus() {
        assertThrows(AvailableException.class, () ->
                bookingService.approve(user2.getId(), booking2.getId(), true));
    }


    @Test
    void get() {
        BookingInfoDto bookingGet = bookingService.get(user2.getId(), booking.getId());
        assertThat(bookingGet).isNotNull();
        assertThat(bookingGet.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingGet.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingGet.getBooker().getId()).isEqualTo(user2.getId());
        assertThat(bookingGet.getStart().isEqual(LocalDateTime.of(2024, 12, 4, 20, 0, 0)));
        assertThat(bookingGet.getEnd().isEqual(LocalDateTime.of(2024, 12, 4, 21, 0, 0)));
    }

    @Test
    void getNotOwnerAndBooker() {
        assertThrows(UserNotOwnerException.class, () ->
                bookingService.approve(5L, booking2.getId(), true));
    }

    @Test
    void getByBookerStatePast() {
        BookingInfoDto bookingPast = BookingMapper.toBookingInfoDto(booking2);
        List<BookingInfoDto> bookingsPast = bookingService.getByBooker(user.getId(), State.PAST);
        assertEquals(bookingsPast, List.of(bookingPast));
    }

    @Test
    void getByBookerStateCurrent() {
        BookingInfoDto bookingCurrent = BookingMapper.toBookingInfoDto(booking);
        List<BookingInfoDto> bookingsCurrent = bookingService.getByBooker(user2.getId(), State.CURRENT);
        assertEquals(bookingsCurrent, List.of(bookingCurrent));
    }

    @Test
    void getByBookerStateFuture() {
        BookingInfoDto bookingFuture = BookingMapper.toBookingInfoDto(booking3);
        List<BookingInfoDto> bookingsFuture = bookingService.getByBooker(user.getId(), State.FUTURE);
        assertEquals(bookingsFuture, List.of(bookingFuture));
    }

    @Test
    void getByBookerStateWaiting() {
        BookingInfoDto bookingWaiting = BookingMapper.toBookingInfoDto(booking);
        List<BookingInfoDto> bookingsWaiting = bookingService.getByBooker(user2.getId(), State.WAITING);
        assertEquals(bookingsWaiting, List.of(bookingWaiting));
    }

    @Test
    void getByBookerStateRejected() {
        BookingInfoDto bookingRejected = BookingMapper.toBookingInfoDto(booking3);
        List<BookingInfoDto> bookingsRejected = bookingService.getByBooker(user.getId(), State.REJECTED);
        assertEquals(bookingsRejected, List.of(bookingRejected));
    }

    @Test
    void getByBookerStateAll() {
        BookingInfoDto bookingRejected2 = BookingMapper.toBookingInfoDto(booking2);
        BookingInfoDto bookingRejected3 = BookingMapper.toBookingInfoDto(booking3);
        List<BookingInfoDto> bookingsRejected = bookingService.getByBooker(user.getId(), State.ALL);
        assertEquals(bookingsRejected, List.of(bookingRejected3, bookingRejected2));
    }

    @Test
    void getByOwnerStatePast() {
        BookingInfoDto bookingPast = BookingMapper.toBookingInfoDto(booking2);
        List<BookingInfoDto> bookingsPast = bookingService.getByOwner(user2.getId(), State.PAST);
        assertEquals(bookingsPast, List.of(bookingPast));
    }

    @Test
    void getByOwnerStateCurrent() {
        BookingInfoDto bookingCurrent = BookingMapper.toBookingInfoDto(booking);
        List<BookingInfoDto> bookingsCurrent = bookingService.getByOwner(user2.getId(), State.CURRENT);
        assertEquals(bookingsCurrent, List.of(bookingCurrent));
    }

    @Test
    void getByOwnerStateFuture() {
        BookingInfoDto bookingFuture = BookingMapper.toBookingInfoDto(booking3);
        List<BookingInfoDto> bookingsFuture = bookingService.getByOwner(user2.getId(), State.FUTURE);
        assertEquals(bookingsFuture, List.of(bookingFuture));
    }

    @Test
    void getByOwnerStateWaiting() {
        BookingInfoDto bookingWaiting = BookingMapper.toBookingInfoDto(booking);
        List<BookingInfoDto> bookingsWaiting = bookingService.getByOwner(user2.getId(), State.WAITING);
        assertEquals(bookingsWaiting, List.of(bookingWaiting));
    }

    @Test
    void getByOwnerStateRejected() {
        BookingInfoDto bookingRejected = BookingMapper.toBookingInfoDto(booking3);
        List<BookingInfoDto> bookingsRejected = bookingService.getByOwner(user2.getId(), State.REJECTED);
        assertEquals(bookingsRejected, List.of(bookingRejected));
    }

    @Test
    void getByOwnerStateAll() {
        BookingInfoDto bookingRejected1 = BookingMapper.toBookingInfoDto(booking);
        BookingInfoDto bookingRejected2 = BookingMapper.toBookingInfoDto(booking2);
        BookingInfoDto bookingRejected3 = BookingMapper.toBookingInfoDto(booking3);
        List<BookingInfoDto> bookingsRejected = bookingService.getByOwner(user2.getId(), State.ALL);
        assertEquals(bookingsRejected, List.of(bookingRejected3, bookingRejected1, bookingRejected2));
    }
}