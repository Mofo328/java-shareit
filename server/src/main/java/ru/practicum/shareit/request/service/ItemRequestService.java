package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllRequests(Long userId);

    List<ItemRequestDto> getAllRequestsForUser(Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId);
}
