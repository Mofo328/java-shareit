package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void testUserDto() {
        Long expectedId = 1L;
        String expectedName = "name";
        String expectedEmail = "mail@example.com";

        UserDto userDto = new UserDto(expectedId, expectedName, expectedEmail);

        assertEquals(expectedId, userDto.getId());
        assertEquals(expectedName, userDto.getName());
        assertEquals(expectedEmail, userDto.getEmail());

        assertNotNull(userDto.toString());

        UserDto anotherUserDto = new UserDto(expectedId, expectedName, expectedEmail);
        assertEquals(userDto, anotherUserDto);
        assertEquals(userDto.hashCode(), anotherUserDto.hashCode());

        UserDto emptyUserDto = new UserDto();
        assertNotNull(emptyUserDto);
        assertNull(emptyUserDto.getId());
        assertNull(emptyUserDto.getName());
        assertNull(emptyUserDto.getEmail());
    }
}