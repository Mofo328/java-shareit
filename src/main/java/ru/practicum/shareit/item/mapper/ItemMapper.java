package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public class ItemMapper {
    public static ItemDto itemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toItem(User user, ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user
        );
    }

    public static ItemBookingInfoDto toItemBookingInfoDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return new ItemBookingInfoDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                comments,
                lastBooking,
                nextBooking)
                ;
    }
}
