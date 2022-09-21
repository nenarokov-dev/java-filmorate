package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.mappers.IntegerMapper;
import ru.yandex.practicum.filmorate.exception.FriendshipError;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.HashSet;
import java.util.Set;

@Component
public class FriendshipDaoImpl implements FriendshipDao {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Integer> getFriendsByUser(Integer id) {
        return new HashSet<>(jdbcTemplate.query(
                String.format("SELECT friend_or_follower_id FROM friendship WHERE user_id=%d", id),
                new IntegerMapper("friend_or_follower_id")));
    }

    @Override
    public String addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("INSERT INTO friendship VALUES (?,?)", userId, friendId);
        return String.format("Пользователь user_id=%d добавился в друзья к пользователю user_id=%d",
                userId, friendId);
    }

    @Override
    public String removeFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id=? AND friend_or_follower_id=?", userId, friendId);
        return String.format("Пользователь user_id=%d успешно удалил из друзей пользователя user_id=%d",
                userId, friendId);
    }

    @Override
    public FriendshipStatus getFriendshipStatus(Integer userId, Integer friendId) {

        Set<Integer> UsersListOfFriends = getFriendsByUser(userId);
        Set<Integer> FriendsListOfFriends = getFriendsByUser(friendId);
        if (UsersListOfFriends.contains(friendId) && !FriendsListOfFriends.contains(userId)) {
            return FriendshipStatus.UNCONFIRMED;
        } else if (!UsersListOfFriends.contains(friendId) && FriendsListOfFriends.contains(userId)) {
            return FriendshipStatus.UNCONFIRMED;
        } else if (UsersListOfFriends.contains(friendId) && FriendsListOfFriends.contains(userId)) {
            return FriendshipStatus.CONFIRMED;
        } else {
            throw new FriendshipError("Ошибка при получении статуса дружбы пользователей user_id=" + userId +
                    " и user_id=" + friendId + ". Пользователи не дружат.");
        }
    }
}
