package ru.practicum.shareit.booking.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

class BookingDtoTest {

    @Test
    void testBookingDtoAllArgsConstructorAndGetters() {
        Long expectedId = 1L;
        LocalDateTime expectedStart = LocalDateTime.of(2023, 10, 1, 10, 0);
        LocalDateTime expectedEnd = LocalDateTime.of(2023, 10, 1, 12, 0);
        Long expectedItemId = 2L;
        Long expectedBooker = 3L;
        BookingStatus expectedStatus = BookingStatus.APPROVED;

        BookingDto bookingDto = new BookingDto(expectedId, expectedStart, expectedEnd, expectedItemId, expectedBooker, expectedStatus);

        assertEquals(expectedId, bookingDto.getId());
        assertEquals(expectedStart, bookingDto.getStart());
        assertEquals(expectedEnd, bookingDto.getEnd());
        assertEquals(expectedItemId, bookingDto.getItemId());
        assertEquals(expectedBooker, bookingDto.getBooker());
        assertEquals(expectedStatus, bookingDto.getStatus());
    }
}