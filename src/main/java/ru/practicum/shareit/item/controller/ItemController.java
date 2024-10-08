package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Получены данные для создания предмета {} у пользователя по ID {}", itemDto, userId);
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{item-id}")
    public ItemDto getById(@PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на получение предмета по ID {}", itemId);
        return itemService.get(itemId);
    }


    @PatchMapping("/{item-id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("item-id") Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Получены данные пользователя по id: {}, предмета по ID: {}, данные для обнавления {}", userId, itemId, itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Список предметов пользователя с id {}", userId);
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Текст для поиска {}", text);
        return itemService.search(text);
    }
}
