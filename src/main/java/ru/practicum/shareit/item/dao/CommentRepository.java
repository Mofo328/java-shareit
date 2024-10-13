package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Comment.full")
    List<Comment> findAllByItemId(Long itemId);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "Comment.full")
    List<Comment> findByItemIdIn(List<Long> items);
}
