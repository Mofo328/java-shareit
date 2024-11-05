package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Collections;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemDto() throws Exception {


        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "description",
                LocalDateTime.of(2024, 11, 1, 2, 2, 2),
                Collections.emptyList()
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-11-01T02:02:02");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(Collections.emptyList());

    }
}
