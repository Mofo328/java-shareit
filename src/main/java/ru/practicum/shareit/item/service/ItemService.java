package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemBookingInfoDto get(Long userId);

    List<ItemBookingInfoDto> getOwnerItems(Long id);

    List<ItemDto> search(String query);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
