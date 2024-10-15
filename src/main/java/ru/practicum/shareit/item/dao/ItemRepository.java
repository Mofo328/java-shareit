package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Item.owner")
    List<Item> findAllByOwnerId(Long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Item.owner")
    Optional<Item> findById(Long userId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Item.owner")
    @Query("FROM Item AS it " +
            "WHERE " +
            "it.available = true " +
            "AND " +
            "(LOWER(it.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(it.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Item> searchAvailableByText(String text);
}
