package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStore {
    Item create(Item item);

    Item update(Item item);

    Item get(Long id);

    List<Item> getALl();

    List<Item> getOwnerItems(Long id);

    List<Item> search(String query);
}
