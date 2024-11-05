package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public class ItemMapper {
    public static ItemDto itemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        } else {
            itemDto.setRequestId(null);
        }
        return itemDto;
    }

    public static Item toItem(User user, ItemDto itemDto, ItemRequest request) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                request
        );
    }

    public static ItemBookingInfoDto toItemBookingInfoDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
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
