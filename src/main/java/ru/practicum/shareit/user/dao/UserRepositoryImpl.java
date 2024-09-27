package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.RequestConflictException;
import ru.practicum.shareit.user.model.User;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private Long counter = 1L;

    @Override
    public User create(User user) {
        user.setId(generateId());
        user.setName(user.getName());
        checkEmail(user);
        user.setEmail(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User updateUser = users.get(user.getId());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkEmail(user);
            updateUser.setEmail(user.getEmail());
        }
        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    private void checkEmail(User user) {
        if (users.values().stream()
                .anyMatch(founder -> founder.getEmail().equals(user.getEmail()))) {
            throw new RequestConflictException("Email уже существует");
        }
    }

    private Long generateId() {
        return counter++;
    }

}
