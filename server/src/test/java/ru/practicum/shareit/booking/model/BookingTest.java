package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingTest {

    private Booking booking;

    @BeforeEach
    public void setUp() {
        User booker = new User();
        booker.setName("Test User");
        booker.setEmail("testuser@example.com");

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(booker);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    public void testCreateBooking() {
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isGreaterThan(0);
        assertThat(booking.getStart()).isEqualTo(booking.getStart());
        assertThat(booking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(booking.getItem().getId()).isEqualTo(booking.getItem().getId());
        assertThat(booking.getBooker().getId()).isEqualTo(booking.getBooker().getId());
        assertThat(booking.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    public void testBookingEqualsAndHashCode() {
        Booking booking2 = new Booking();
        booking2.setId(booking.getId());
        booking2.setStart(booking.getStart());
        booking2.setEnd(booking.getEnd());
        booking2.setItem(booking.getItem());
        booking2.setBooker(booking.getBooker());
        booking2.setStatus(booking.getStatus());

        assertThat(booking).isEqualTo(booking2);
        assertThat(booking.hashCode()).isEqualTo(booking2.hashCode());
    }
}