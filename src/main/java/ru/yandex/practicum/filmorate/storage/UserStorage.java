package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUsersById(Integer id);

    public User putUser(User user);

    User setUser(User user);

}