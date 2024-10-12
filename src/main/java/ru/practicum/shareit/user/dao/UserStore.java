package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStore {
    List<User> getAll();

    void delete(Long id);

    Optional<User> get(Long id);

    User update(User user);

    User create(User user);
}
