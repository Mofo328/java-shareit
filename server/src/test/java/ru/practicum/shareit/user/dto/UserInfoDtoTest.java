package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserInfoDtoTest {

    @Test
    void testUserInfoDto() {
        Long expectedId = 1L;
        UserInfoDto userInfoDto = new UserInfoDto(expectedId);
        assertEquals(expectedId, userInfoDto.getId());
        assertNotNull(userInfoDto.toString());
        UserInfoDto anotherUserInfoDto = new UserInfoDto(expectedId);
        assertEquals(userInfoDto, anotherUserInfoDto);
        assertEquals(userInfoDto.hashCode(), anotherUserInfoDto.hashCode());
    }
}