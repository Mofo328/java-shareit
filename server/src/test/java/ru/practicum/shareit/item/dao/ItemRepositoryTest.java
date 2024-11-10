package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchAvailableByText() {
        User owner = new User();
        owner.setName("Test User2");
        owner.setEmail("testuser2@example.com");

        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        List<Item> items = itemRepository.searchAvailableByText("name");
        Assertions.assertTrue(items.get(0).getName().contains(item.getName()));
    }
}