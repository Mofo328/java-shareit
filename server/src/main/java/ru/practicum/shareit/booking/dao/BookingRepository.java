package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    Optional<Booking> findById(Long itemId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> getAllByBookerIdOrderByStartDesc(Long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> findByItemIdIn(List<Long> itemIds);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId AND " +
            "((b.start <= :end) AND (b.end >= :start))")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> findByItemIdAndIntersection(Long itemId, LocalDateTime start, LocalDateTime end);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> findAllByItemId(Long itemId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> getAllByItemOwnerIdOrderByStartDesc(Long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> getAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> getByBookerIdAndStatus(Long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :id AND b.end < :time AND upper(b.status) = UPPER('APPROVED')" +
            "ORDER BY b.start DESC")
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Booking.full")
    List<Booking> getByBookerIdStatePast(Long id, LocalDateTime time);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end >= :time AND :time >= b.start " +
            "ORDER BY b.start DESC")
    List<Booking> getByBookerIdStateCurrent(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > :time ORDER BY b.start DESC")
    List<Booking> getByBookerIdStateFuture(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE  i.owner.id = :userId AND b.start > :time " +
            "ORDER BY b.start DESC")
    List<Booking> getByOwnerIdStateFuture(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId " +
            "AND b.start <= :time AND b.end >= :time ORDER BY b.start DESC ")
    List<Booking> getByOwnerIdStateCurrent(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b JOIN b.item i ON b.item = i WHERE i.owner.id = :userId AND b.end < :time")
    List<Booking> getByOwnerIdStatePast(Long userId, LocalDateTime time);
}
