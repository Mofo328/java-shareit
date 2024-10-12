package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя нет")
        );
        Item item = ItemMapper.toItem(owner, itemDto);
        return ItemMapper.itemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя нет"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи нет"));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setId(itemId);
        return ItemMapper.itemDto(itemRepository.save(item));
    }

    @Override
    public ItemBookingInfoDto get(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи нет"));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentsDto = comments.stream().map(CommentMapper::toCommentDto).toList();
        List<Booking> itemBookings = bookingRepository.findAllByItemIdAndEndBefore(itemId, LocalDateTime.now());
        Booking lastBooking = getLastBooking(itemBookings);
        Booking nextBooking = getNextBooking(itemBookings);
        ItemBookingInfoDto itemBookingInfoDto = ItemMapper.toItemBookingInfoDto(item, lastBooking, nextBooking, commentsDto);
        return itemBookingInfoDto;
    }

    @Override
    public List<ItemBookingInfoDto> getOwnerItems(Long ownerId) {
        List<Item> ownerItems = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemsIds = ownerItems.stream().map(Item::getId).toList();
        List<Booking> bookings = bookingRepository.findByItemIdInAndEndAfter(itemsIds, LocalDateTime.now());
        Map<Long, List<Booking>> bookingsMapByItemsId = new HashMap<>();
        for (Booking booking : bookings) {
            bookingsMapByItemsId.computeIfAbsent(booking.getItem().getId(), k -> new ArrayList<>()).add(booking);
        }
        List<ItemBookingInfoDto> itemBookingInfoDto = new ArrayList<>();

        for (Item item : ownerItems) {
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            List<CommentDto> commentDto = comments.stream().map(CommentMapper::toCommentDto).toList();
            List<Booking> itemBookings = bookingsMapByItemsId.getOrDefault(item.getId(), new ArrayList<>());
            Booking lastBooking = getLastBooking(itemBookings);
            Booking nextBooking = getNextBooking(itemBookings);
            ItemBookingInfoDto infoDto = ItemMapper.toItemBookingInfoDto(item, lastBooking, nextBooking, commentDto);
            itemBookingInfoDto.add(infoDto);
        }
        return itemBookingInfoDto;
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя нет"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи нет"));
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        List<Booking> booking = bookingRepository.getByBookerIdStatePast(comment.getAuthor().getId(), LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new BadRequestException("Нет бронирований");
        }
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<ItemDto> search(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableByText(query).stream().map(ItemMapper::itemDto).collect(Collectors.toList());
    }

    private Booking getLastBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart() != null)
                .max(Comparator.comparing(Booking::getStart));
        return lastBooking.orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getEnd() != null)
                .max(Comparator.comparing(Booking::getEnd));
        return lastBooking.orElse(null);
    }
}

