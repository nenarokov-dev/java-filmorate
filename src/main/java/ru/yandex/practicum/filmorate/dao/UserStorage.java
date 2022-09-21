package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    User save(User user);

    void update(User user);

    User getUsersById(Integer userId);

    List<User> getAllUsers();

    Set<User> listOfFriends(Integer userId);

    Set<User> commonFriends(Integer userId, Integer otherUserId);

}
