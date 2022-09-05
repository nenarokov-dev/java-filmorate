package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> userStorage = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User getUsersById(Integer id) {
        return userStorage.get(id);
    }

    @Override
    public User putUser(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User setUser(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    public boolean isNotContains(Integer id) {
        return !userStorage.containsKey(id);
    }


}
