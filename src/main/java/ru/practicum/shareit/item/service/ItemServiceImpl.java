package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.get(userId).orElseThrow(() ->
                new NotFoundException("Пользователя нет")
        );
        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.itemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.get(userId).orElseThrow(() -> new NotFoundException("Пользователя нет"));
        Item item = ItemMapper.toItem(owner, itemDto);
        item.setId(itemId);
        return ItemMapper.itemDto(itemRepository.update(item));
    }

    @Override
    public ItemDto get(Long id) {
        return ItemMapper.itemDto(itemRepository.get(id));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long id) {
        return itemRepository.getOwnerItems(id).stream().map(ItemMapper::itemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String query) {
        return itemRepository.search(query).stream().map(ItemMapper::itemDto).collect(Collectors.toList());
    }
}

