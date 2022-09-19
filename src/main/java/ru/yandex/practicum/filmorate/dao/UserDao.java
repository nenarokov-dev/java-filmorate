package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface UserDao {

    User setUser(User user) throws SQLException;

    User putUser(User user) throws SQLException;

    User getUsersById(Integer userId);

    List<User> getAllUsers();

    String addFriend(Integer userId, Integer friendId) throws SQLException;

    String removeFriend(Integer userId, Integer friendId);

    Set<User> listOfFriends(Integer userId);

    Set<User> commonFriends(Integer userId, Integer otherUserId);

    FriendshipStatus getFriendshipStatus(Integer userId, Integer friendId);

    Integer getMaxUserId();
}