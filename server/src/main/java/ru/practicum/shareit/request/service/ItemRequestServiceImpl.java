package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {


    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователя нет"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    public List<ItemRequestDto> getAllRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterId(userId);
        return getItemRequestsWithItem(requests);
    }

    public List<ItemRequestDto> getAllRequestsForUser(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId);
        return getItemRequestsWithItem(requests);
    }

    public ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя нет"));
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Нет запроса"));
        List<ItemDto> items = itemRepository.findByRequestId(itemRequest.getId())
                .stream().map(ItemMapper::itemDto).toList();
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    private List<ItemRequestDto> getItemRequestsWithItem(List<ItemRequest> requests) {
        List<Long> requestsIds = requests.stream().map(ItemRequest::getId).toList();
        List<Item> itemsByRequests = itemRepository.findByRequestIdIn(requestsIds);
        Map<Long, List<Item>> itemsMapByItemRequestsId = new HashMap<>();
        for (Item item : itemsByRequests) {
            itemsMapByItemRequestsId.computeIfAbsent(item.getRequest().getId(), i -> new ArrayList<>()).add(item);
        }
        List<ItemRequestDto> itemRequests = new ArrayList<>();
        for (ItemRequest itemRequest : requests) {
            List<ItemDto> itemsDto = itemsMapByItemRequestsId.getOrDefault(itemRequest.getId(), new ArrayList<>())
                    .stream().map(ItemMapper::itemDto).toList();
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, itemsDto);
            itemRequests.add(itemRequestDto);
        }
        return itemRequests.stream().sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed()).toList();
    }
}
