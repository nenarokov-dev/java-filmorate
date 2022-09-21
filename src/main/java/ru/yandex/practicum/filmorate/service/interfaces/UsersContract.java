package ru.yandex.practicum.filmorate.service.interfaces;


import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface UsersContract {

    User save(User user) throws SQLException;

    User update(User user) throws SQLException;

    User getUsersById(Integer id) throws SQLException;

    List<User> getAllUsers() throws SQLException;

    String addFriend(Integer userId, Integer friendId) throws SQLException;

    String removeFriend(Integer userId, Integer friendId) throws SQLException;

    Set<User> listOfFriends(Integer userId) throws SQLException;

    Set<User> commonFriends(Integer userId, Integer otherUserId) throws SQLException;

    FriendshipStatus getFriendshipStatus(Integer userId, Integer friendId) throws SQLException;

}
