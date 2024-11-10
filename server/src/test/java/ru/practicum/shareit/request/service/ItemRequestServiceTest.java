package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ItemRequestService itemRequestService;

    private User requester;
    private User owner;

    private Item itemCreated;

    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setName("Name");
        requester.setEmail("email@yandex.ru");
        userRepository.save(requester);

        owner = new User();
        owner.setName("Name2");
        owner.setEmail("email222@yandex.ru");
        userRepository.save(owner);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(requester);
        itemRequestRepository.save(itemRequest);

        itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("Description2");
        itemRequest2.setCreated(LocalDateTime.now().plusDays(1L));
        itemRequest2.setRequester(requester);
        itemRequestRepository.save(itemRequest2);

        itemCreated = new Item();
        itemCreated.setName("name");
        itemCreated.setDescription("description");
        itemCreated.setRequest(itemRequest);
        itemCreated.setOwner(owner);
        itemCreated.setAvailable(true);
        itemRepository.save(itemCreated);
    }

    @Test
    void create() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("Dd");
        ItemRequestDto itemRequestCreated = itemRequestService.create(requester.getId(), itemRequestDto);
        assertThat(itemRequestCreated).isNotNull();
        assertThat(itemRequestCreated.getDescription()).isEqualTo("Dd");
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDto> itemRequestsDto = itemRequestService.getAllRequests(requester.getId());
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(requester.getId(), itemRequest.getId());
        ItemRequestDto itemRequestDto2 = itemRequestService.getItemRequestById(requester.getId(), itemRequest2.getId());
        assertThat(itemRequestsDto).isEqualTo(List.of(itemRequestDto2, itemRequestDto));
        assertThat(itemRequestsDto.get(1).getItems()).isEqualTo(List.of(ItemMapper.itemDto(itemCreated)));
    }

    @Test
    void getAllRequestsForUser() {
        List<ItemRequestDto> itemRequestsDto = itemRequestService.getAllRequestsForUser(owner.getId());
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(requester.getId(), itemRequest.getId());
        ItemRequestDto itemRequestDto2 = itemRequestService.getItemRequestById(requester.getId(), itemRequest2.getId());
        assertThat(itemRequestsDto).isEqualTo(List.of(itemRequestDto2, itemRequestDto));
        assertThat(itemRequestsDto.get(1).getItems()).isEqualTo(List.of(ItemMapper.itemDto(itemCreated)));
    }

    @Test
    void getItemRequestById() {
        ItemRequestDto itemRequestGet = itemRequestService.getItemRequestById(requester.getId(), itemRequest.getId());
        assertThat(itemRequestGet).isNotNull();
        assertThat(itemRequestGet.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestGet.getCreated()).isEqualTo(itemRequest.getCreated());
        assertThat(itemRequestGet.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestGet.getItems()).isEqualTo(List.of(ItemMapper.itemDto(itemCreated)));
    }

    @Test
    void failNotExistItemRequestGet() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(requester.getId(), 4L));
    }

    @Test
    void failNotExistUserGet() {
        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(4L, itemRequest.getId()));
    }
}