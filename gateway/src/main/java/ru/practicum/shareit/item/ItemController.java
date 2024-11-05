package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Создать предмет={}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto, @PathVariable Long id) {
        log.info("Обновить предмет с ID= {}", id);
        return itemClient.updateItem(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long id) {
        log.info("Получить предмет по id= {}", id);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> retrieveAllItem(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получить все предметы пользователя = {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByKeyword(@RequestParam(name = "text", defaultValue = "") String query,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск по запросу={}", query);
        return itemClient.searchItemByKeyword(query, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        log.info("Добавить комент предмету с ID = {} от пользователя с ID = {}", itemId, userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}