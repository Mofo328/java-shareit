package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingInfoDto create(Long userId, BookingDto bookingDto);

    BookingInfoDto approve(Long userId, Long bookingId, Boolean approved);

    BookingInfoDto get(Long userId, Long bookingId);

    List<BookingInfoDto> getByBooker(Long userId, State state);

    List<BookingInfoDto> getByOwner(Long userId, State state);
}
