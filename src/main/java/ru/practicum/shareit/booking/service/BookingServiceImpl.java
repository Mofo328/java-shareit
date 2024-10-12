package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingInfoDto create(Long userId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещи нет"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя нет"));
        if (!item.getAvailable()) {
            throw new AvailableException("Вещь уже занята");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public BookingInfoDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("бронирование не найдено"));
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new UserNotOwnerException("Пользователь не является хозяином вещи");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) || booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new AvailableException("невозможно изменить статус");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking);
    }

    @Override
    public BookingInfoDto get(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("бронирование не найдено"));
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new UserNotOwnerException("Пользователь не является хозяином вещи");
        }
        return BookingMapper.toBookingInfoDto(booking);
    }

    public List<BookingInfoDto> getByBooker(Long userId, State state) {
        List<Booking> bookings;
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.getAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.getByBookerIdStateCurrent(userId, time);
                break;
            case PAST:
                bookings = bookingRepository.getByBookerIdStatePast(userId, time);
                break;
            case FUTURE:
                bookings = bookingRepository.getByBookerIdStateFuture(userId, time);
                break;
            case WAITING:
                bookings = bookingRepository.getByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.getByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Ошибка состояния");
        }
        return bookings.stream().map(BookingMapper::toBookingInfoDto).toList();
    }

    public List<BookingInfoDto> getByOwner(Long userId, State state) {
        List<Booking> bookings;
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.getAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.getByOwnerIdStateCurrent(userId, time);
                break;
            case PAST:
                bookings = bookingRepository.getByOwnerIdStatePast(userId, time);
                break;
            case FUTURE:
                bookings = bookingRepository.getByOwnerIdStateFuture(userId, time);
                break;
            case WAITING:
                bookings = bookingRepository.getAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.getAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Ошибка состояния");
        }
        return bookings.stream().map(BookingMapper::toBookingInfoDto).toList();
    }

}
