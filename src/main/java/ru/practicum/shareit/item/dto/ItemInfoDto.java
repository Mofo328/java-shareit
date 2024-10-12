package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ItemInfoDto {
    private final Long id;
    private final String name;

}