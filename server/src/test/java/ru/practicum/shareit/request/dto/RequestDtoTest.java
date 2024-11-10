package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RequestDtoTest {

    @Test
    public void testItemRequestDto() {

        Long id = 1L;
        String description = "Описание запроса";
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto dto = new ItemRequestDto(id, description, created, Collections.emptyList());

        assertEquals(id, dto.getId());
        assertEquals(description, dto.getDescription());
        assertEquals(created, dto.getCreated());
        assertEquals(Collections.emptyList(), dto.getItems());

        ItemRequestDto emptyDto = new ItemRequestDto();
        assertNull(emptyDto.getId());
        assertNull(emptyDto.getDescription());
        assertNull(emptyDto.getCreated());
        assertNull(emptyDto.getItems());
    }
}