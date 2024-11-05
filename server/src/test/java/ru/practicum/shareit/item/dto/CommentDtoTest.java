package ru.practicum.shareit.item.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CommentDtoTest {

    @Test
    void testCommentDtoConstructorAndGetters() {
        Long expectedId = 1L;
        String expectedText = "This is a comment.";
        String expectedAuthorName = "John Doe";
        LocalDateTime expectedCreated = LocalDateTime.now();

        CommentDto commentDto = new CommentDto(expectedId, expectedText, expectedAuthorName, expectedCreated);


        assertEquals(expectedId, commentDto.getId());
        assertEquals(expectedText, commentDto.getText());
        assertEquals(expectedAuthorName, commentDto.getAuthorName());
        assertEquals(expectedCreated, commentDto.getCreated());
    }
}