package ru.practicum.shareit.item.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ItemDtoTest {

    @Test
    void testItemDtoAllArgsConstructorAndGetters() {
        Long expectedId = 1L;
        String expectedName = "Name";
        String expectedDescription = "Description";
        Boolean expectedAvailable = true;
        Long expectedRequest = 100L;

        ItemDto itemDto = new ItemDto(expectedId, expectedName, expectedDescription, expectedAvailable, expectedRequest);

        assertEquals(expectedId, itemDto.getId());
        assertEquals(expectedName, itemDto.getName());
        assertEquals(expectedDescription, itemDto.getDescription());
        assertEquals(expectedAvailable, itemDto.getAvailable());
        assertEquals(expectedRequest, itemDto.getRequestId());
    }
}