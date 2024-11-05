package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemTest {
    private Item item;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("testuser@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
    }

    @Test
    public void testCreateBooking() {
        assertThat(item).isNotNull();
        assertThat(item.getId()).isGreaterThan(0);
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Description");
        assertThat(item.getOwner().getId()).isEqualTo(1L);
    }

    @Test
    public void testBookingEqualsAndHashCode() {
        Item item2 = new Item();
        item2.setId(item.getId());
        item2.setName(item.getName());
        item2.setDescription(item.getDescription());
        item2.setAvailable(item.getAvailable());
        item2.setOwner(item.getOwner());
        assertThat(item).isEqualTo(item2);
        assertThat(item.hashCode()).isEqualTo(item2.hashCode());
    }
}
