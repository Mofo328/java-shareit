package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemBookingInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private Booking lastBooking;
    private Booking nextBooking;
}
