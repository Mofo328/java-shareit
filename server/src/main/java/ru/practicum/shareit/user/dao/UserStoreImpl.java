package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.RequestConflictException;
import ru.practicum.shareit.user.model.User;


import java.util.*;

@Repository
public class UserStoreImpl implements UserStore {

    private final Map<Long, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();

    private Long counter = 1L;

    @Override
    public User create(User user) {
        user.setId(generateId());
        checkEmail(user);
        emails.add(user.getEmail());
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
            emails.remove(updateUser.getEmail());
            updateUser.setEmail(user.getEmail());
            emails.add(updateUser.getEmail());
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
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        emails.remove(users.get(id).getEmail());
        users.remove(id);

    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    private void checkEmail(User user) {
        if (emails.contains(user.getEmail())) {
            throw new RequestConflictException("Такой емейл уже существует");
        }
    }

    private Long generateId() {
        return counter++;
    }
}
