package ru.practicum.shareit.item.dao;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Repository
public class ItemStoreImpl implements ItemStore {

    private final Map<Long, List<Item>> ownerItems = new HashMap<>();

    private final Map<Long, Item> items = new HashMap<>();

    private Long counter = 1L;

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        List<Item> userItems = ownerItems.computeIfAbsent(item.getOwner().getId(), list -> new ArrayList<>());
        userItems.add(item);
        ownerItems.put(item.getOwner().getId(), userItems);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item updateItem = items.get(item.getId());
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        items.put(updateItem.getId(), updateItem);
        List<Item> userItems = ownerItems.get(item.getOwner().getId());
        userItems.add(updateItem);
        ownerItems.put(updateItem.getOwner().getId(), userItems);
        return updateItem;
    }

    @Override
    public Item get(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getALl() {
        return items.values().stream().toList();
    }

    @Override
    public List<Item> getOwnerItems(Long id) {
        return ownerItems.get(id);
    }

    @Override
    public List<Item> search(String query) {
        if (query.isBlank() || query.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
    }

    private Long generateId() {
        return counter++;
    }
}
