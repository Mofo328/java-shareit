package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
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

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя нет")
        );
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        }
        Item item = ItemMapper.toItem(owner, itemDto, itemRequest);
        return ItemMapper.itemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя нет"));
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
        List<Booking> itemBookings = bookingRepository.findAllByItemId(itemId);
        BookingDto lastBooking = getLastBooking(itemBookings);
        BookingDto nextBooking = getNextBooking(itemBookings);
        ItemBookingInfoDto itemBookingInfoDto = ItemMapper.toItemBookingInfoDto(item, lastBooking, nextBooking, commentsDto);
        return itemBookingInfoDto;
    }

    @Override
    public List<ItemBookingInfoDto> getOwnerItems(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователя нет"));
        List<Item> ownerItems = itemRepository.findAllByOwnerId(ownerId);
        List<Long> itemsIds = ownerItems.stream().map(Item::getId).toList();
        Map<Long, List<Booking>> bookingsMapByItemsId = getItemsBookings(itemsIds);
        Map<Long, List<Comment>> commentsMapByItemsID = getItemsComment(itemsIds);
        List<ItemBookingInfoDto> itemBookingInfoDto = new ArrayList<>();
        for (Item item : ownerItems) {
            List<CommentDto> commentDto = commentsMapByItemsID.getOrDefault(item.getId(), new ArrayList<>())
                    .stream().map(CommentMapper::toCommentDto).toList();
            List<Booking> itemBookings = bookingsMapByItemsId.getOrDefault(item.getId(), new ArrayList<>());
            BookingDto lastBooking = getLastBooking(itemBookings);
            BookingDto nextBooking = getNextBooking(itemBookings);
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

    private Map<Long, List<Booking>> getItemsBookings(List<Long> itemsIds) {
        List<Booking> bookings = bookingRepository.findByItemIdIn(itemsIds);
        Map<Long, List<Booking>> bookingsMapByItemsId = new HashMap<>();
        for (Booking booking : bookings) {
            bookingsMapByItemsId.computeIfAbsent(booking.getItem().getId(), k -> new ArrayList<>()).add(booking);
        }
        return bookingsMapByItemsId;
    }

    private Map<Long, List<Comment>> getItemsComment(List<Long> itemsIds) {
        List<Comment> allItemsComments = commentRepository.findByItemIdIn(itemsIds);
        Map<Long, List<Comment>> commentsMapByItemsID = new HashMap<>();
        for (Comment comment : allItemsComments) {
            commentsMapByItemsID.computeIfAbsent(comment.getItem().getId(), c -> new ArrayList<>()).add(comment);
        }
        return commentsMapByItemsID;
    }

    private BookingDto getLastBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<BookingDto> lastBooking = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .filter(booking -> booking.getStart() != null)
                .max(Comparator.comparing(BookingDto::getStart));
        return lastBooking.orElse(null);
    }

    private BookingDto getNextBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<BookingDto> lastBooking = bookings.stream()
                .map(BookingMapper::toBookingDto)
                .filter(booking -> booking.getEnd() != null)
                .max(Comparator.comparing(BookingDto::getEnd));
        return lastBooking.orElse(null);
    }
}

