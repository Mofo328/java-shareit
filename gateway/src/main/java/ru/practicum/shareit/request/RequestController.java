package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {

    private final RequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создали ItemRequest={}", itemRequestDto);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllMyItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Все запросы пользователя={}", userId);
        return itemRequestClient.getAllOwnItemRequests(userId);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemRequestId) {
        log.info("Получили запрос по id={}", itemRequestId);
        return itemRequestClient.getItemRequest(userId, itemRequestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsForUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получить все запросы пользователей, для пользователя с id={}", userId);
        return itemRequestClient.getAllUserRequest(userId);
    }
}